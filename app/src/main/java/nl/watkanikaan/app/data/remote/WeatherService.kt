package nl.watkanikaan.app.data.remote

import nl.watkanikaan.app.data.model.WeatherEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("Wat-kan-ik-aan/{area}.json")
    suspend fun getWeather(
        @Path("area") location: String,
    ): Response<WeatherEntity>
}
