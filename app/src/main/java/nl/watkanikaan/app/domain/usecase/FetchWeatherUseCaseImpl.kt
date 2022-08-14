package nl.watkanikaan.app.domain.usecase

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
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
    private val defaultSharedPrefs: SharedPref,
) : FetchWeatherUseCase {

    data class Params(val forceRefresh: Boolean? = null)

    override fun execute(
        params: Params
    ): Flow<Result<Weather?>> {
        val location = defaultSharedPrefs.getLocationSetting() ?: LatLng(52.148958, 5.375144)
        val area = when {
            PolyUtil.containsLocation(location, north, true) -> WeatherArea.North()
            PolyUtil.containsLocation(location, northWest, true) -> WeatherArea.NorthWest()
            PolyUtil.containsLocation(location, east, true) -> WeatherArea.East()
            PolyUtil.containsLocation(location, southWest, true) -> WeatherArea.SouthWest()
            PolyUtil.containsLocation(location, southEast, true) -> WeatherArea.SouthEast()
            else -> WeatherArea.Middle()
        }

        return repository.fetchWeather(
            area.jsonKey, params.forceRefresh
        ).map { response ->
            val weather = response.data ?: return@map response

            val updatedResponse = weather.copy(
                // override location with country area
                location = area.localisedAreaName,
                forecast = weather.forecast.map {
                    // convert temperature to windchill temperature
                    it.copy(windChillTemp = windChill(it.windChillTemp, it.windSpeed))
                })

            return@map response.map(updatedResponse)
        }
    }

    /** JAG/TI method to calculate windchill temperature */
    private fun windChill(
        t: Double,
        w: Double,
    ): Double {
        return round(
            13.12 + 0.6215 * t - 11.37 * (w * 3.6).pow(0.16) + 0.3965 * t * (w * 3.6).pow(
                0.16
            )
        )
    }
}