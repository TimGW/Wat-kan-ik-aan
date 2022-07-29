package nl.watkanikaan.app.domain.model

import nl.watkanikaan.app.R

data class Weather(
    val location: String,
    val dayExpectation: String,
    val weatherAlarm: String,
    val modifiedAt: Long,
    val forecast: Map<Day, Forecast>,
) {
    enum class Day {
        NOW,
        TODAY,
        TOMORROW,
        DAY_AFTER_TOMORROW;

        // todo remove from model and remove boolean
        fun toText(isBreak: Boolean = false) = when (this) {
            NOW -> R.string.now
            TODAY -> R.string.today
            TOMORROW -> R.string.tomorrow
            DAY_AFTER_TOMORROW -> if (isBreak) R.string.day_after_tomorrow_break else R.string.day_after_tomorrow
        }
    }

    data class Forecast(
        val dewPoint: Int?,
        val weatherIcon: String,
        val temperature: Double,
        val windForce: Int,
        val windSpeed: Double,
        val chanceOfPrecipitation: Int,
        val chanceOfSun: Int,
    )
}
