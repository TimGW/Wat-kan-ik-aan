package nl.watkanikaan.app.domain.model

import nl.watkanikaan.app.R

data class Weather(
    val location: String,
    val dayExpectation: String,
    val weatherAlarm: String,
    val modifiedAt: Long,
    val forecast: List<Forecast>,
) {

    enum class Day {
        NOW,
        TODAY,
        TOMORROW,
        DAY_AFTER_TOMORROW;

        fun toText() = when (this) {
            NOW -> R.string.now
            TODAY -> R.string.today
            TOMORROW -> R.string.tomorrow
            DAY_AFTER_TOMORROW -> R.string.day_after_tomorrow
        }
    }

    data class Forecast(
        val day: Day,
        val dewPoint: Int?,
        val weatherIcon: String,
        val windChillTemp: Double,
        val windForce: Int,
        val windSpeed: Double,
        val chanceOfPrecipitation: Int,
        val chanceOfSun: Int,
        val sunUp: Int,
        val sunUnder: Int,
    )
}
