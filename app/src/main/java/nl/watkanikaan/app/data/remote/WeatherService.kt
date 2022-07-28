package nl.watkanikaan.app.data.remote

import nl.watkanikaan.app.BuildConfig
import nl.watkanikaan.app.data.model.WeatherEntity
import nl.watkanikaan.app.data.model.WeatherJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("json-data-10min.php")
    suspend fun getWeather(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("locatie") location: String,
    ): Response<WeatherEntity>
}
