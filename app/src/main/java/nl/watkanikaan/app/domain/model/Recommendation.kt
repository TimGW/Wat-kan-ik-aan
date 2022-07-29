package nl.watkanikaan.app.domain.model

import nl.watkanikaan.app.R

data class Recommendation(
    val selectedDay: Weather.Day = Weather.Day.NOW,
    val jacket: Jacket = Jacket.NORMAL,
    val top: Top = Top.T_SHIRT,
    val bottom: Bottom = Bottom.LONG,
    val extras: Set<Extra> = emptySet(),
) {
    enum class Jacket(val type: Int) {
        NONE(R.string.none),
        WINTER(R.string.jacket_cold),
        NORMAL(R.string.jacket_normal),
        SUMMER(R.string.jacket_warm),
    }

    enum class Top(val type: Int) {
        T_SHIRT(R.string.top_tshirt),
        VEST(R.string.top_tshirt),
        SWEATER(R.string.top_sweater),
    }

    enum class Bottom(val type: Int) {
        SHORTS(R.string.bottom_shorts),
        LONG(R.string.bottom_long),
    }

    enum class Extra(val message: Int?) {
        RAIN(R.string.extra_rain),
        RAIN_WINDY(R.string.extra_rain_windy),
        FREEZING(R.string.extra_freezing),
        SUNNY(R.string.extra_sunny),
        MUGGY(R.string.extra_muggy),
    }
}