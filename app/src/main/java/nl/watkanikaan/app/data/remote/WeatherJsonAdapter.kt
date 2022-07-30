package nl.watkanikaan.app.data.remote

import com.squareup.moshi.FromJson
import nl.watkanikaan.app.data.model.WeatherEntity
import nl.watkanikaan.app.data.model.WeatherJson
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.ui.or
import nl.watkanikaan.app.ui.toDoubleOr
import nl.watkanikaan.app.ui.toIntOr
import java.text.ParseException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WeatherJsonAdapter {

    @FromJson
    fun fromJson(weatherJson: WeatherJson): WeatherEntity? {
        val result: WeatherJson.WeatherDataJson =
            weatherJson.liveWeatherJson.firstOrNull() ?: return null

        return WeatherEntity(
            location = result.location.or(),
            dayExpectation = result.dayExpectation.or(),
            forecast = result.expectationDbModel(),
            weatherAlarm = result.weatherAlarm.or(),
        )
    }

    private fun WeatherJson.WeatherDataJson.expectationDbModel() = buildMap {
        put(
            Weather.Day.NOW, WeatherEntity.Forecast(
                dewPoint = dewPoint?.toIntOrNull(),
                weatherIcon = image.or(),
                temperature = temperature.toDoubleOr(),
                windForce = windForce.toIntOr(),
                windSpeed = windSpeedMs.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationToday.toIntOr(),
                chanceOfSun = chanceOfSunToday.toIntOr(),
            )
        )
        put(
            Weather.Day.TODAY, WeatherEntity.Forecast(
                dewPoint = null,
                weatherIcon = weatherIconToday.or(),
                temperature = mapTempExpectation(sunUpAt, minTempToday, maxTempToday),
                windForce = windForceToday.toIntOr(),
                windSpeed = windSpeedMsToday.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationToday.toIntOr(),
                chanceOfSun = chanceOfSunToday.toIntOr(),
            )
        )
        put(
            Weather.Day.TOMORROW, WeatherEntity.Forecast(
                dewPoint = null,
                weatherIcon = weatherIconTomorrow.or(),
                temperature = maxTempTomorrow.toDoubleOr(),
                windForce = windForceTomorrow.toIntOr(),
                windSpeed = windSpeedMsTomorrow.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationTomorrow.toIntOr(),
                chanceOfSun = chanceOfSunTomorrow.toIntOr(),
            )
        )
        put(
            Weather.Day.DAY_AFTER_TOMORROW, WeatherEntity.Forecast(
                dewPoint = null,
                weatherIcon = weatherIconDayAfterTomorrow.or(),
                temperature = maxTempDayAfterTomorrow.toDoubleOr(),
                windForce = windForceDayAfterTomorrow.toIntOr(),
                windSpeed = windSpeedMsDayAfterTomorrow.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationDayAfterTomorrow.toIntOr(),
                chanceOfSun = chanceOfSunDayAfterTomorrow.toIntOr(),
            )
        )
    }

    private fun mapTempExpectation(
        sunUpAt: String?,
        min: String?,
        max: String?
    ): Double {
        val now = LocalDateTime.now().hour
        val sunUpAtHour = try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(sunUpAt, formatter).hour
        } catch (e: ParseException) {
            sunUpAt?.substring(0, 2).toIntOr()
        }
        return if (now > WARMEST_HOUR_OF_DAY || now < sunUpAtHour) {
            min.toDoubleOr()
        } else {
            max.toDoubleOr()
        }
    }

    companion object {
        private const val WARMEST_HOUR_OF_DAY = 16
    }
}