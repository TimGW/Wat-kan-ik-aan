package nl.watkanikaan.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import nl.watkanikaan.app.R

@Parcelize
data class Weather(
    val location: String,
    val dayExpectation: String,
    val weatherAlarm: String,
    val modifiedAt: Long,
    val forecast: Map<Day, Forecast>,
) : Parcelable {

    @Parcelize
    enum class Day(val position: Int) : Parcelable {
        NOW(0),
        TODAY(1),
        TOMORROW(2),
        DAY_AFTER_TOMORROW(3);

        fun findDay(position: Int) = values().find { it.position == position }

        // todo remove from model and remove boolean
        fun toText(isBreak: Boolean = false) = when (this) {
            NOW -> R.string.now
            TODAY -> R.string.today
            TOMORROW -> R.string.tomorrow
            DAY_AFTER_TOMORROW -> if (isBreak) R.string.day_after_tomorrow_break else R.string.day_after_tomorrow
        }
    }

    @Parcelize
    data class Forecast(
        val dewPoint: Int?,
        val weatherIcon: String,
        val windChillTemp: Double,
        val windForce: Int,
        val windSpeed: Double,
        val chanceOfPrecipitation: Int,
        val chanceOfSun: Int,
        val sunUp: Int,
        val sunUnder: Int,
    ) : Parcelable
}
