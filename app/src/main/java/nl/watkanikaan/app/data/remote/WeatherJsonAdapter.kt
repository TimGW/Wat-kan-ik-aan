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

    private fun WeatherJson.WeatherDataJson.expectationDbModel() = buildList {
        val sunUp = parseSunTime(sunUpAt)
        val sunUnder = parseSunTime(sunUnderAt)

        add(
            WeatherEntity.Forecast(
                day = Weather.Day.NOW,
                dewPoint = dewPoint?.toIntOrNull(),
                weatherIcon = image.or(),
                temperature = temperature.toDoubleOr(),
                windForce = windForce.toIntOr(),
                windSpeed = windSpeedMs.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationToday.toIntOr(),
                chanceOfSun = chanceOfSunToday.toIntOr(),
                sunUp = sunUp,
                sunUnder = sunUnder,
            )
        )
        add(
            WeatherEntity.Forecast(
                day = Weather.Day.TODAY,
                dewPoint = null,
                weatherIcon = weatherIconToday.or(),
                temperature = mapTempExpectation(sunUpAt, minTempToday, maxTempToday),
                windForce = windForceToday.toIntOr(),
                windSpeed = windSpeedMsToday.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationToday.toIntOr(),
                chanceOfSun = chanceOfSunToday.toIntOr(),
                sunUp = sunUp,
                sunUnder = sunUnder,
            )
        )
        add(
            WeatherEntity.Forecast(
                day = Weather.Day.TOMORROW,
                dewPoint = null,
                weatherIcon = weatherIconTomorrow.or(),
                temperature = maxTempTomorrow.toDoubleOr(),
                windForce = windForceTomorrow.toIntOr(),
                windSpeed = windSpeedMsTomorrow.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationTomorrow.toIntOr(),
                chanceOfSun = chanceOfSunTomorrow.toIntOr(),
                sunUp = sunUp,
                sunUnder = sunUnder,
            )
        )
        add(
            WeatherEntity.Forecast(
                day = Weather.Day.DAY_AFTER_TOMORROW,
                dewPoint = null,
                weatherIcon = weatherIconDayAfterTomorrow.or(),
                temperature = maxTempDayAfterTomorrow.toDoubleOr(),
                windForce = windForceDayAfterTomorrow.toIntOr(),
                windSpeed = windSpeedMsDayAfterTomorrow.toDoubleOr(),
                chanceOfPrecipitation = chanceOfPrecipitationDayAfterTomorrow.toIntOr(),
                chanceOfSun = chanceOfSunDayAfterTomorrow.toIntOr(),
                sunUp = sunUp,
                sunUnder = sunUnder,
            )
        )
    }

    private fun parseSunTime(sun: String?): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(sun, formatter).hour
        } catch (e: ParseException) {
            sun?.substring(0, 2).toIntOr()
        }
    }

    fun mapTempExpectation(
        sunUpAt: String?,
        min: String?,
        max: String?
    ): Double {
        val now = LocalDateTime.now().hour
        return if (now > WARMEST_HOUR_OF_DAY || now < parseSunTime(sunUpAt)) {
            min.toDoubleOr()
        } else {
            max.toDoubleOr()
        }
    }

    companion object {
        private const val WARMEST_HOUR_OF_DAY = 16
    }
}