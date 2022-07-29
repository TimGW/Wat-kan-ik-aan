package nl.watkanikaan.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import nl.watkanikaan.app.R
import nl.watkanikaan.app.databinding.FragmentWeatherBinding
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var weatherAdapter: WeatherItemAdapter
    private val refresh by lazy {
        throttleFirst(THROTTLE_LIMIT, lifecycleScope, viewModel::refresh) {
            binding.swiperefresh.isRefreshing = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.onBackPressedDispatcher?.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swiperefresh.setOnRefreshListener(refresh)

        binding.weatherRv.apply {
            weatherAdapter = WeatherItemAdapter { day, forecast ->
                viewModel.updateRecommendation(day, forecast)
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater) // FIXME refactor
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                view?.findNavController()?.navigate(WeatherFragmentDirections.showAppSettings())
                true
            }
            else -> super.onOptionsItemSelected(item)
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
            binding.jacket.text = getString(jacket.type)
            binding.top.text = getString(top.type)
            binding.bottom.text = getString(bottom.type)
            binding.extra.text = extras.joinToString("en, ") { extra ->
                extra.message?.let { getString(it) }?.toString().orEmpty()
            }
        }
    }

    companion object {
        const val THROTTLE_LIMIT = 60L * 1000L
    }
}
