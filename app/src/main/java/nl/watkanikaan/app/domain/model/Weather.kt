package nl.watkanikaan.app.domain.model

import kotlin.math.pow

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
        DAY_AFTER_TOMORROW
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

fun windChill(
    t: Double,
    w: Int,
): Double {
    return 13.12 + 0.6215 * t - 11.37 * (w * 3.6).pow(0.16) + 0.3965 * t * (w * 3.6).pow(0.16)
}

//data class Weather(
//    val location: String,
//    val feelingTemperature: Float,
//    val summary: String,
//    val dewPoint: Int,
//    val windDirection: String,
//    val windForce: Int,
//    val dayExpectation: String,
//    val sunUpAt: String,
//    val sunUnderAt: String,
//    val image: String,
//    val expectations: List<Expectation>, // todo update to map
//    val weatherAlarm: String,
//    val createdAt: Long? = null,
//    val modifiedAt: Long? = null,
//) {
//    data class Expectation(
//        val weatherIcon: String,
//        val maxTemp: Int,
//        val minTemp: Int,
//        val windForce: Int,
//        val chanceOfPrecipitation: Int,
//        val chanceOfSun: Int,
//    )
//}
