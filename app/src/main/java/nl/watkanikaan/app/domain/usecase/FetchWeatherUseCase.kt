package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.watkanikaan.app.data.local.SharedPrefs
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.round

class FetchWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
    sharedPrefs: SharedPrefs,
) : UseCase<FetchWeatherUseCase.Params, Flow<@JvmSuppressWildcards Result<Weather?>>> {
    private val location = sharedPrefs.getLocationSetting()

    data class Params(val forceRefresh: Boolean? = null)

    override fun execute(
        params: Params
    ): Flow<Result<Weather?>> = repository.fetchWeather(
        location, params.forceRefresh
    ).map { response ->
        val weather = response.data ?: return@map response

        // convert temperature to windchill temperature
        val updatedResponse = weather.copy(forecast = weather.forecast.mapValues {
            val entry = it.value
            entry.copy(temperature = windChill(entry.temperature, entry.windSpeed))
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