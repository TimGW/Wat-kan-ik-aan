package nl.watkanikaan.app.domain.usecase

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.repository.WeatherRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class FetchWeatherUseCaseImplTest {
    private val repository: WeatherRepository = mock()
    private val sharedPrefs: SharedPref = mock()
    private val setup: () -> FetchWeatherUseCaseImpl = {
        `when`(sharedPrefs.getLocationSetting()).thenReturn(LatLng(1.1, 1.2))
        FetchWeatherUseCaseImpl(repository, sharedPrefs)
    }
    private val anyForecasts = listOf(
        Weather.Forecast(
            Weather.Day.NOW,
            null,
            "",
            20.0,
            0,
            10.0,
            0,
            0,
            5,
            21
        )
    )


    @Test
    fun testUseCase_callsRepo() {
        val forceRefresh = false

        setup().execute(FetchWeatherUseCaseImpl.Params(forceRefresh))

        Mockito.verify(repository).fetchWeather("mid", false)
    }

    @Test
    fun testUseCase_mapTemp() = runTest {
        val forceRefresh = false

        `when`(repository.fetchWeather(any(), any())).thenReturn(flow {
            Result.Success(
                Weather(
                    location = "Amsterdam",
                    dayExpectation = "zonnig",
                    weatherAlarm = "",
                    modifiedAt = 0,
                    forecast = anyForecasts
                )
            )
        })

        setup().execute(FetchWeatherUseCaseImpl.Params(forceRefresh)).collect {
            assertEquals(
                21.0,
                (it as Result.Success).data?.forecast?.first()?.windChillTemp
            )
        }
    }
}