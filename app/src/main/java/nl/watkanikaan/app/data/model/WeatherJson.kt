package nl.watkanikaan.app.data.model

import com.squareup.moshi.Json

data class WeatherJson(
    @field:Json(name = "liveweer") val liveWeatherJson: List<WeatherDataJson>
) {
    data class WeatherDataJson(
        // actual data
        @field:Json(name = "plaats") val location: String? = null,
        @field:Json(name = "temp") val temperature: String? = null,
        @field:Json(name = "gtemp") val feelingTemperature: String? = null,
        @field:Json(name = "samenv") val summary: String? = null,
        @field:Json(name = "lv") val relativeAirHumidity: String? = null,
        @field:Json(name = "windr") val windDirection: String? = null,
        @field:Json(name = "winds") val windForce: String? = null,
        @field:Json(name = "windkmh") val windSpeedKmh: String? = null,
        @field:Json(name = "windms") val windSpeedMs: String? = null,
        @field:Json(name = "luchtd") val airPressure: String? = null,
        @field:Json(name = "dauwp") val dewPoint: String? = null,
        @field:Json(name = "zicht") val visibilityKm: String? = null,
        @field:Json(name = "verw") val dayExpectation: String? = null,
        @field:Json(name = "sup") val sunUpAt: String? = null,
        @field:Json(name = "sunder") val sunUnderAt: String? = null,
        @field:Json(name = "image") val image: String? = null,

        // today data
        @field:Json(name = "d0weer") val weatherIconToday: String? = null,
        @field:Json(name = "d0tmax") val maxTempToday: String? = null,
        @field:Json(name = "d0tmin") val minTempToday: String? = null,
        @field:Json(name = "d0windk") val windForceToday: String? = null,
        @field:Json(name = "d0windkmh") val windSpeedKmhToday: String? = null,
        @field:Json(name = "d0windms") val windSpeedMsToday: String? = null,
        @field:Json(name = "d0windr") val windDirectionToday: String? = null,
        @field:Json(name = "d0neerslag") val chanceOfPrecipitationToday: String? = null,
        @field:Json(name = "d0zon") val chanceOfSunToday: String? = null,

        // tomorrow data
        @field:Json(name = "d1weer") val weatherIconTomorrow: String? = null,
        @field:Json(name = "d1tmax") val maxTempTomorrow: String? = null,
        @field:Json(name = "d1tmin") val minTempTomorrow: String? = null,
        @field:Json(name = "d1windk") val windForceTomorrow: String? = null,
        @field:Json(name = "d1windkmh") val windSpeedKmhTomorrow: String? = null,
        @field:Json(name = "d1windms") val windSpeedMsTomorrow: String? = null,
        @field:Json(name = "d1windr") val windDirectionTomorrow: String? = null,
        @field:Json(name = "d1neerslag") val chanceOfPrecipitationTomorrow: String? = null,
        @field:Json(name = "d1zon") val chanceOfSunTomorrow: String? = null,

        // day after tomorrow data
        @field:Json(name = "d2weer") val weatherIconDayAfterTomorrow: String? = null,
        @field:Json(name = "d2tmax") val maxTempDayAfterTomorrow: String? = null,
        @field:Json(name = "d2tmin") val minTempDayAfterTomorrow: String? = null,
        @field:Json(name = "d2windk") val windForceDayAfterTomorrow: String? = null,
        @field:Json(name = "d2windkmh") val windSpeedKmhDayAfterTomorrow: String? = null,
        @field:Json(name = "d2windms") val windSpeedMsDayAfterTomorrow: String? = null,
        @field:Json(name = "d2windr") val windDirectionDayAfterTomorrow: String? = null,
        @field:Json(name = "d2neerslag") val chanceOfPrecipitationDayAfterTomorrow: String? = null,
        @field:Json(name = "d2zon") val chanceOfSunDayAfterTomorrow: String? = null,

        @field:Json(name = "alarm") val weatherAlarm: String? = null,
    )
}