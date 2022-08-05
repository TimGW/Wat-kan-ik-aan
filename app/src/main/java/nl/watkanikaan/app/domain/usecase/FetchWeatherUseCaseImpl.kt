package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.repository.WeatherRepository
import nl.watkanikaan.app.domain.usecase.marker.FetchWeatherUseCase
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.round

class FetchWeatherUseCaseImpl @Inject constructor(
    private val repository: WeatherRepository,
    defaultSharedPrefs: SharedPref,
) : FetchWeatherUseCase {
    private val location = defaultSharedPrefs.getLocationSetting()

    data class Params(val forceRefresh: Boolean? = null)

    override fun execute(
        params: Params
    ): Flow<Result<Weather?>> = repository.fetchWeather(
        location, params.forceRefresh
    ).map { response ->
        val weather = response.data ?: return@map response

        // convert temperature to windchill temperature
        val updatedResponse = weather.copy(forecast = weather.forecast.map {
            it.copy(windChillTemp = windChill(it.windChillTemp, it.windSpeed))
        })

        return@map response.map(updatedResponse)
    }

    /** JAG/TI method to calculate windchill temperature */
    private fun windChill(
        t: Double,
        w: Double,
    ): Double {
        return round(13.12 + 0.6215 * t - 11.37 * (w * 3.6).pow(0.16) + 0.3965 * t * (w * 3.6).pow(0.16))
    }
}