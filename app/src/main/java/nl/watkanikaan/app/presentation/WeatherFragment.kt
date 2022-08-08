package nl.watkanikaan.app.presentation

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mittylabs.library.PermissionsHelper
import dagger.hilt.android.AndroidEntryPoint
import nl.watkanikaan.app.BuildConfig
import nl.watkanikaan.app.R
import nl.watkanikaan.app.databinding.FragmentWeatherBinding
import nl.watkanikaan.app.domain.model.Movement
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.presentation.forecast.ForecastItemAdapter
import nl.watkanikaan.app.presentation.forecast.ForecastItemDecoration
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WeatherFragment : Fragment(), MenuProvider, PermissionsHelper.PermissionListener {
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var forecastAdapter: ForecastItemAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedPosition: Int = 0
    private val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var permissionsHelper: PermissionsHelper

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_EXTRA_SELECTED_POS, selectedPosition)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        permissionsHelper = PermissionsHelper(context, this, permission.ACCESS_COARSE_LOCATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(BUNDLE_EXTRA_SELECTED_POS, selectedPosition)
        } else {
            permissionsHelper.check()
        }

        binding.loadingWeatherRv.loadingNow.loadingOverline.text = getText(R.string.now)
        binding.loadingWeatherRv.loadingToday.loadingOverline.text = getText(R.string.today)
        binding.loadingWeatherRv.loadingTomorrow.loadingOverline.text = getText(R.string.tomorrow)
        binding.loadingWeatherRv.loadingDayAfterTomorrow.loadingOverline.text =
            getText(R.string.day_after_tomorrow)
        binding.swiperefresh.setOnRefreshListener(viewModel::refresh)
        binding.chipsContainer.chipRestMovement.setOnClickListener {
            viewModel.selectMovement(Movement.Rest)
        }
        binding.chipsContainer.chipLightMovement.setOnClickListener {
            viewModel.selectMovement(Movement.Light)
        }
        binding.chipsContainer.chipHeavyMovement.setOnClickListener {
            viewModel.selectMovement(Movement.Heavy)
        }
        binding.weatherRv.apply {
            forecastAdapter =
                ForecastItemAdapter(selectedPosition) { forecast, position, isLongClick ->
                    selectedPosition = position
                    viewModel.selectDay(forecast)
                    if (isLongClick) showForecastDialog(forecast)
                }
            adapter = forecastAdapter
            layoutManager = FlexboxLayoutManager(
                requireContext(),
            ).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.SPACE_BETWEEN
                flexWrap = FlexWrap.NOWRAP
            }

            overScrollMode = View.OVER_SCROLL_NEVER
            isNestedScrollingEnabled = false
            addItemDecoration(
                ForecastItemDecoration(
                    resources.getDimension(R.dimen.keyline_4).toInt()
                )
            )
        }

        observeWeather()
        observeRecommendation()
    }

    private fun showForecastDialog(
        forecast: Weather.Forecast
    ) {
        val title = getString(
            R.string.alert_dialog_forecast_title,
            getString(forecast.day.toText()).lowercase()
        )
        val nl = "\n"
        val message = buildString {
            append(getString(R.string.alert_dialog_forecast_msg_temp, forecast.windChillTemp), nl)
            append(
                getString(
                    R.string.alert_dialog_forecast_msg_rain,
                    forecast.chanceOfPrecipitation
                ), nl
            )
            append(getString(R.string.alert_dialog_forecast_msg_sun, forecast.chanceOfSun), nl)
            append(getString(R.string.alert_dialog_forecast_msg_wind, forecast.windForce), nl)
            append(getString(R.string.alert_dialog_forecast_msg_sunup, forecast.sunUp), nl)
            append(getString(R.string.alert_dialog_forecast_msg_sununder, forecast.sunUnder))
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshRecommendation()// fixme: is called while viewmodel resets to old state, so the recommendation doesn't match the weather
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.options_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_settings -> {
                view?.findNavController()?.navigate(WeatherFragmentDirections.showAppSettings())
                true
            }
            else -> false
        }
    }

    private fun observeWeather() {
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            binding.swiperefresh.isRefreshing = false
            val updatedAt = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(weather.modifiedAt))
            binding.location.text = getString(R.string.location, weather.location)
            binding.lastUpdated.text = getString(R.string.last_updated, updatedAt)
            forecastAdapter.updateItems(weather.forecast)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = false
            binding.root.snackbar(getString(it))
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swiperefresh.isRefreshing = false
            if (isLoading) {
                binding.loadingWeatherRv.root.visibility = View.VISIBLE
                binding.loadingContent.root.visibility = View.VISIBLE
            } else {
                fadeIn(binding.loadingWeatherRv.root, binding.weatherRv)
                fadeIn(binding.loadingContent.root, binding.content.root)
            }
        }
    }

    private fun observeRecommendation() = launchAfter {
        viewModel.recommendation.collect { result ->
            if (result == null) return@collect

            with(result) {
                viewModel.updateToolbarTitle(selectedDay)

                binding.content.jacket.visibility = if (jacket?.type == null) {
                    View.GONE
                } else {
                    binding.content.jacket.text = getString(jacket.type)
                    View.VISIBLE
                }
                binding.content.top.text = getString(top.type)
                binding.content.bottom.text = getString(bottom.type)
                binding.content.extra.visibility = if (extras.isEmpty()) {
                    View.GONE
                } else {
                    binding.content.extra.text = extras.joinToString("en, ") { extra ->
                        extra.message?.let { getString(it) }?.toString().orEmpty()
                    }
                    View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionGranted() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) {
                view?.snackbar(message = getString(R.string.location_not_found))
            } else {
                viewModel.updateLocation(location)
            }
        }
    }

    override fun onPermissionRationale(permissionRequestAction: () -> Unit) {
        binding.root.snackbar(
            message = getString(R.string.location_permissions),
            actionMessage = getString(R.string.location_settings),
            action = { permissionRequestAction.invoke() })
    }

    override fun onPermissionDenied() {
        binding.root.snackbar(
            message = getString(R.string.location_permissions),
            actionMessage = getString(R.string.location_settings),
            action = {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                })
            })
    }

    companion object {
        const val BUNDLE_EXTRA_SELECTED_POS = "BUNDLE_EXTRA_SELECTED_POS"
    }
}
