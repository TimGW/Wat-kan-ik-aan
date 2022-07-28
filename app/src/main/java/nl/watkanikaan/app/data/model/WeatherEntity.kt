package nl.watkanikaan.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import nl.watkanikaan.app.domain.model.Weather

@Entity
data class WeatherEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "modified_at") val modifiedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "day_expectation") val dayExpectation: String,
    @ColumnInfo(name = "forecast") val forecast: Map<Weather.Day, Forecast>,
    @ColumnInfo(name = "alarm") val weatherAlarm: String,
) {
    data class Forecast(
        @ColumnInfo(name = "dew_point") val dewPoint: Int?,
        @ColumnInfo(name = "icon") val weatherIcon: String,
        @ColumnInfo(name = "temp") val temperature: Double,
        @ColumnInfo(name = "wind_force") val windForce: Int,
        @ColumnInfo(name = "wind_speed") val windSpeed: Double,
        @ColumnInfo(name = "chance_of_precipitation") val chanceOfPrecipitation: Int,
        @ColumnInfo(name = "chance_of_sun") val chanceOfSun: Int,
    )

    companion object {
        // TODO extract to mapper
        fun from(weather: Weather) = with(weather) {
            WeatherEntity(
                modifiedAt = modifiedAt,
                location = location,
                dayExpectation = dayExpectation,
                forecast = forecast.mapValues {
                    Forecast(
                        it.value.dewPoint,
                        it.value.weatherIcon,
                        it.value.temperature,
                        it.value.windForce,
                        it.value.windSpeed,
                        it.value.chanceOfPrecipitation,
                        it.value.chanceOfSun
                    )
                },
                weatherAlarm = weatherAlarm,
            )
        }
    }

    // TODO extract to mapper
    fun toWeather() = Weather(
        modifiedAt = modifiedAt,
        location = location,
        dayExpectation = dayExpectation,
        forecast = forecast.mapValues {
            Weather.Forecast(
                it.value.dewPoint,
                it.value.weatherIcon,
                it.value.temperature,
                it.value.windForce,
                it.value.windSpeed,
                it.value.chanceOfPrecipitation,
                it.value.chanceOfSun
            )
        },
        weatherAlarm = weatherAlarm,
    )
}