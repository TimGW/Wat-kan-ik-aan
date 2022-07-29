package nl.watkanikaan.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nl.watkanikaan.app.data.local.WeatherDao
import nl.watkanikaan.app.data.model.WeatherEntity
import nl.watkanikaan.app.data.remote.WeatherService
import nl.watkanikaan.app.domain.model.IoDispatcher
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.repository.ErrorHandler
import nl.watkanikaan.app.domain.repository.WeatherRepository
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val weatherDao: WeatherDao,
    private val errorHandler: ErrorHandler,
    @IoDispatcher private val networkDispatcher: CoroutineDispatcher,
) : WeatherRepository {

    companion object {
        private const val REFRESH_HOURLY_INTERVAL = 6L // hours
    }

    override fun fetchWeather(
        location: String?,
        forceRefresh: Boolean?,
    ) = object : NetworkBoundResource<WeatherEntity, Weather?>(errorHandler) {
        override suspend fun saveRemoteData(response: WeatherEntity) {
            weatherDao.insertWithTimestamp(response)
        }

        override fun fetchFromLocal() = weatherDao.getWeatherDistinctUntilChanged().map {
            it?.toWeather()
        }

        override suspend fun fetchFromRemote(): Response<WeatherEntity> {
            val loc = location ?: "Amsterdam"
            return weatherService.getWeather(location = loc)
        }

        override fun shouldFetch(
            data: Weather?
        ): Boolean {
            return (data == null || forceRefresh == true || isWeatherStale(data.modifiedAt))
        }
    }.asFlow().flowOn(networkDispatcher)

    private fun isWeatherStale(lastUpdated: Long?): Boolean {
        val interval = TimeUnit.HOURS.toMillis(REFRESH_HOURLY_INTERVAL)
        return (System.currentTimeMillis() - interval) > lastUpdated ?: return true
    }
}