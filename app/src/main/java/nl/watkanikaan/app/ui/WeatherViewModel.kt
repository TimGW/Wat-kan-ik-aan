package nl.watkanikaan.app.ui

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.watkanikaan.app.domain.model.Movement
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.usecase.CalcRecommendationUseCase
import nl.watkanikaan.app.domain.usecase.FetchWeatherUseCase
import nl.watkanikaan.app.domain.usecase.UpdateLocationUseCase
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val fetchWeatherUseCase: FetchWeatherUseCase,
    private val calcRecommendationUseCase: CalcRecommendationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
) : ViewModel() {
    private var throttleJob: Job? = null
    private var selectedMovement: Movement = Movement.Rest
    private var selectedForecast: Weather.Forecast? = null

    private val _weather = MutableStateFlow<Result<Weather?>>(Result.Loading(null))
    val weather: StateFlow<Result<Weather?>> = _weather.asStateFlow()

    private val _recommendation = MutableStateFlow<Recommendation?>(null)
    val recommendation: StateFlow<Recommendation?> = _recommendation.asStateFlow()

    private val _dismissLoader = MutableLiveData<Unit>()
    val dismissLoader: LiveData<Unit> = _dismissLoader

    private val _toolbar = MutableLiveData<Int>()
    val toolbar: LiveData<Int> = _toolbar

    init {
        fetchWeather()
    }

    fun refresh() {
        fetchWeather(forceRefresh = true)
    }

    fun selectDay(
        forecast: Weather.Forecast,
    ) = viewModelScope.launch {
        selectedForecast = forecast
        executeRecommendation(forecast = forecast)
    }

    fun selectMovement(
        movement: Movement
    ) = viewModelScope.launch {
        selectedMovement = movement
        executeRecommendation(movement = movement)
    }

    fun updateToolbarTitle(selectedDay: Weather.Day) {
        _toolbar.value = selectedDay.toText()
    }

    fun updateLocation(location: Location) {
        updateLocationUseCase.execute(UpdateLocationUseCase.Params(location))
    }

    fun refreshRecommendation() = viewModelScope.launch {
        executeRecommendation()
    }

    private fun fetchWeather(
        forceRefresh: Boolean = false
    ) {
        if (throttleJob?.isCompleted != false) {
            throttleJob = viewModelScope.launch {
                collectWeatherFlow(forceRefresh)
                delay(if (forceRefresh) TIMEOUT else 0)
            }
        } else {
            _dismissLoader.value = Unit
        }
    }

    private suspend fun collectWeatherFlow(forceRefresh: Boolean) = fetchWeatherUseCase.execute(
        FetchWeatherUseCase.Params(forceRefresh = forceRefresh)
    ).collect { result: Result<Weather?> ->
        val forecast: List<Weather.Forecast>? = result.data?.forecast

        if (forceRefresh) {
            _weather.update { result }

            forecast
                ?.find { it.day == selectedForecast?.day }
                ?.also { executeRecommendation(forecast = it) }
        } else {
            _weather.value = result

            forecast
                ?.find { it.day == Weather.Day.NOW }
                ?.also {
                    executeRecommendation(
                        forecast = it,
                        movement = Movement.Rest
                    )
                }
        }
    }

    private suspend fun executeRecommendation(
        forecast: Weather.Forecast? = null,
        movement: Movement? = null
    ) {
        val f = forecast ?: selectedForecast ?: _weather.value.data?.forecast?.first() ?: return
        val m = movement ?: selectedMovement

        calcRecommendationUseCase.execute(
            CalcRecommendationUseCase.Params(f, m)
        ).collect { result ->
            _recommendation.value = result
        }
    }

    companion object {
        private const val TIMEOUT = 10L * 1000L // 10 sec
    }
}
