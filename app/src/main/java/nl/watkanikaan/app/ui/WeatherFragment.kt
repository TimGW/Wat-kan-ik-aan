package nl.watkanikaan.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import nl.watkanikaan.app.BuildConfig
import nl.watkanikaan.app.R
import nl.watkanikaan.app.databinding.FragmentWeatherBinding
import nl.watkanikaan.app.databinding.LayoutChipsMovementBinding
import nl.watkanikaan.app.databinding.LayoutContentBinding
import nl.watkanikaan.app.domain.model.Movement
import nl.watkanikaan.app.domain.model.Profile
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WeatherFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var movementBinding: LayoutChipsMovementBinding
    private lateinit var contentBinding: LayoutContentBinding
    private lateinit var weatherAdapter: WeatherItemAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedPosition: Int = 0
    private val viewModel: WeatherViewModel by activityViewModels()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocationUpdate()
        } else {
            showPermissionRationale { openSettings() }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_EXTRA_SELECTED_POS, selectedPosition)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        val root = binding.root

        movementBinding = LayoutChipsMovementBinding.bind(root)
        contentBinding = LayoutContentBinding.bind(root)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(BUNDLE_EXTRA_SELECTED_POS, selectedPosition)
        }

        binding.swiperefresh.setOnRefreshListener(viewModel::refresh)

        movementBinding.chipRestMovement.setOnClickListener {
            viewModel.selectMovement(Movement.Rest)
        }
        movementBinding.chipLightMovement.setOnClickListener {
            viewModel.selectMovement(Movement.Light)
        }
        movementBinding.chipHeavyMovement.setOnClickListener {
            viewModel.selectMovement(Movement.Heavy)
        }

        binding.weatherRv.apply {
            weatherAdapter = WeatherItemAdapter(selectedPosition) { forecast, position ->
                selectedPosition = position
                viewModel.selectDay(forecast)
            }
            adapter = weatherAdapter
            layoutManager = FlexboxLayoutManager(
                requireContext(),
            ).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.SPACE_BETWEEN
                flexWrap = FlexWrap.NOWRAP
            }

            overScrollMode = View.OVER_SCROLL_NEVER
            isNestedScrollingEnabled = false
            addItemDecoration(OffsetDecoration(resources.getDimension(R.dimen.keyline_4).toInt()))
        }

        observeWeather()
        observeRecommendation()
        getLocationUpdate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshRecommendation()
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

    private fun observeWeather() = launchAfter {
        viewModel.weather.collect { result ->
            if (result !is Result.Loading) binding.swiperefresh.isRefreshing = false
            result.error?.message?.let { binding.root.snackbar(getString(it)) }
            result.data?.let { updateWeather(it) }
        }
    }

    private fun observeRecommendation() = launchAfter {
        viewModel.recommendation.collect { result ->
            result?.let { updateRecommendationUI(it) }
        }
    }

    private fun updateWeather(weather: Weather) {
        val updatedAt = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(weather.modifiedAt))
        binding.location.text = getString(R.string.location, weather.location)
        binding.lastUpdated.text = getString(R.string.last_updated, updatedAt)
        weatherAdapter.updateItems(weather.forecast)
    }

    private fun updateRecommendationUI(recommendation: Recommendation) {
        with(recommendation) {
            viewModel.updateToolbarTitle(recommendation.selectedDay)

            contentBinding.jacket.visibility = if (jacket?.type == null) {
                View.GONE
            } else {
                contentBinding.jacket.text = getString(jacket.type)
                View.VISIBLE
            }

            contentBinding.top.text = getString(top.type)
            contentBinding.bottom.text = getString(bottom.type)
            contentBinding.extra.text = extras.joinToString("en, ") { extra ->
                extra.message?.let { getString(it) }?.toString().orEmpty()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationUpdate() {
        if (checkPermissions()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location == null) {
                    view?.snackbar(message = getString(R.string.location_not_found))
                } else {
                    viewModel.updateLocation(location)
                }
            }
        }
    }

    private fun showPermissionRationale(block: () -> Unit) {
        binding.root.snackbar(
            message = getString(R.string.location_permissions),
            actionMessage = getString(R.string.location_settings),
            action = { block.invoke() })
    }

    private fun openSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        })
    }

    private fun checkPermissions(): Boolean {
        when {
            isPermissionGranted() -> return true
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                showPermissionRationale {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        return false
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val BUNDLE_EXTRA_SELECTED_POS = "BUNDLE_EXTRA_SELECTED_POS"
    }
}
