package nl.watkanikaan.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.watkanikaan.app.R
import nl.watkanikaan.app.domain.model.Profile
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.usecase.CalcRecommendationUseCase
import nl.watkanikaan.app.domain.usecase.FetchWeatherUseCase
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val fetchWeatherUseCase: FetchWeatherUseCase,
    private val calcRecommendationUseCase: CalcRecommendationUseCase,
) : ViewModel() {

    private val _weather = MutableStateFlow<Result<Weather?>>(Result.Loading(null))
    val weather: StateFlow<Result<Weather?>> = _weather.asStateFlow()

    private val _recommendation = MutableStateFlow<Recommendation?>(null)
    val recommendation: StateFlow<Recommendation?> = _recommendation.asStateFlow()

    init {
        fetchWeatherRecommendation()
    }

    fun refresh() {
        fetchWeatherRecommendation(isRefreshed = true)
    }

    private fun fetchWeatherRecommendation(
        isRefreshed: Boolean? = null
    ) = viewModelScope.launch {
        fetchWeatherUseCase.execute(
            FetchWeatherUseCase.Params(forceRefresh = isRefreshed)
        ).collect { result: Result<Weather?> ->
            val data: Weather? = result.data
//            val forecast = data?.forecast.orEmpty()
//            val weather = result.map(data)
//                data?.copy(forecast = data.forecast.mapIndexed { index, expectation ->
//                    expectation.copy(day = index.toDay())
//                })
//            )

            if (isRefreshed == true) _weather.value = result else _weather.update { result }
            data?.forecast?.get(Weather.Day.NOW)?.let { executeRecommendation(it) }
//            if (data != null)  // todo this override the current selected
        }
    }

    fun updateRecommendation(
        day: Weather.Day,
        weather: Weather.Forecast,
    ) = viewModelScope.launch {
        _weather.value.data?.let { executeRecommendation(weather) }
    }

    private suspend fun executeRecommendation(
        weather: Weather.Forecast,
        profile: Profile = Profile(),
    ) {
        calcRecommendationUseCase.execute(
            CalcRecommendationUseCase.Params(weather, profile)
        ).collect { result ->
            _recommendation.value = result
        }
    }
}