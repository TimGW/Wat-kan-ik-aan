package nl.watkanikaan.app.ui

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.watkanikaan.app.domain.model.Profile
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

    private val _weather = MutableStateFlow<Result<Weather?>>(Result.Loading(null))
    val weather: StateFlow<Result<Weather?>> = _weather.asStateFlow()

    private val _recommendation = MutableStateFlow<Recommendation?>(null)
    val recommendation: StateFlow<Recommendation?> = _recommendation.asStateFlow()

    private val _toolbar = MutableLiveData<Int>()
    val toolbar: LiveData<Int> = _toolbar

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

            if (isRefreshed == true) _weather.value = result else _weather.update { result }

            val now = Weather.Day.NOW
            data?.forecast?.get(now)?.let { executeRecommendation(now, it) }
        }
    }

    fun updateRecommendation(
        day: Weather.Day,
        weather: Weather.Forecast,
    ) = viewModelScope.launch {
        _weather.value.data?.let { executeRecommendation(day, weather) }
    }

    private suspend fun executeRecommendation(
        day: Weather.Day,
        weather: Weather.Forecast,
        movement: Profile.Movement? = null
    ) {
        calcRecommendationUseCase.execute(
            CalcRecommendationUseCase.Params(day, weather, movement)
        ).collect { result ->
            _recommendation.value = result
        }
    }

    fun updateToolbarTitle(selectedDay: Weather.Day) {
        _toolbar.value = selectedDay.toText()
    }

    fun updateLocation(location: Location) {
        updateLocationUseCase.execute(UpdateLocationUseCase.Params(location))
    }

    fun recalculateRecommendation(
        day: Weather.Day,
        movement: Profile.Movement
    ) = viewModelScope.launch {
        val forecast = weather.value.data?.forecast?.get(day) ?: return@launch
        _weather.value.data?.let { executeRecommendation(day, forecast, movement) }
    }

}