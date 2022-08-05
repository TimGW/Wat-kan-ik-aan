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
    @ColumnInfo(name = "forecast") val forecast: List<Forecast>,
    @ColumnInfo(name = "alarm") val weatherAlarm: String,
) {
    data class Forecast(
        @ColumnInfo(name = "day") val day: Weather.Day,
        @ColumnInfo(name = "dew_point") val dewPoint: Int?,
        @ColumnInfo(name = "icon") val weatherIcon: String,
        @ColumnInfo(name = "temp") val temperature: Double,
        @ColumnInfo(name = "wind_force") val windForce: Int,
        @ColumnInfo(name = "wind_speed") val windSpeed: Double,
        @ColumnInfo(name = "chance_of_precipitation") val chanceOfPrecipitation: Int,
        @ColumnInfo(name = "chance_of_sun") val chanceOfSun: Int,
        @ColumnInfo(name = "sun_up_at") val sunUp: Int,
        @ColumnInfo(name = "sun_under_at") val sunUnder: Int,
    )
}