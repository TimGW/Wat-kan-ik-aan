package nl.watkanikaan.app.domain.repository

import kotlinx.coroutines.flow.Flow
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.model.Result

interface WeatherRepository {
    fun fetchWeather(
        lat: Double?,
        long: Double?,
        forceRefresh: Boolean? = false,
    ): Flow<Result<Weather?>>
}
