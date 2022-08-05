package nl.watkanikaan.app.presentation

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.watkanikaan.app.domain.model.Movement
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.usecase.CalcRecommendationUseCaseImpl
import nl.watkanikaan.app.domain.usecase.FetchWeatherUseCaseImpl
import nl.watkanikaan.app.domain.usecase.UpdateLocationUseCaseImpl
import nl.watkanikaan.app.domain.usecase.marker.CalcRecommendationUseCase
import nl.watkanikaan.app.domain.usecase.marker.FetchWeatherUseCase
import nl.watkanikaan.app.domain.usecase.marker.UpdateLocationUseCase
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val fetchWeatherUseCase: FetchWeatherUseCase,
    private val calcRecommendationUseCase: CalcRecommendationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
) : ViewModel() {
    private var selectedMovement: Movement = Movement.Rest
    private var selectedForecast: Weather.Forecast? = null

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading.distinctUntilChanged()

    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int> = _errorMessage.distinctUntilChanged()

    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather> = _weather.distinctUntilChanged()

    private val _recommendation = MutableStateFlow<Recommendation?>(null)
    val recommendation: StateFlow<Recommendation?> = _recommendation.asStateFlow()

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
        updateLocationUseCase.execute(UpdateLocationUseCaseImpl.Params(location))
    }

    fun refreshRecommendation() = viewModelScope.launch {
        executeRecommendation()
    }

    private fun fetchWeather(forceRefresh: Boolean = false) = viewModelScope.launch {
        fetchWeatherUseCase.execute(
            FetchWeatherUseCaseImpl.Params(forceRefresh = forceRefresh)
        ).collect { result: Result<Weather?> ->
            val error = result.error?.message
            val weather = result.data
            val forecast = weather?.forecast

            _isLoading.value = result is Result.Loading && (weather == null && error == null)
            weather?.let { _weather.value = it }
            error?.let { _errorMessage.value = it }

            if (forceRefresh) {
                forecast
                    ?.find { it.day == selectedForecast?.day }
                    ?.also { executeRecommendation(forecast = it) }
            } else {
                forecast
                    ?.find { it.day == Weather.Day.NOW }
                    ?.also { executeRecommendation(forecast = it, movement = Movement.Rest) }
            }
        }
    }

    private suspend fun executeRecommendation(
        forecast: Weather.Forecast? = null,
        movement: Movement? = null
    ) {
        val f = forecast ?: selectedForecast ?: _weather.value?.forecast?.first() ?: return
        val m = movement ?: selectedMovement

        calcRecommendationUseCase.execute(
            CalcRecommendationUseCaseImpl.Params(f, m)
        ).collect { result ->
            _recommendation.value = result
        }
    }
}
