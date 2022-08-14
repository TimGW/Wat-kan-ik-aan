package nl.watkanikaan.app.domain.usecase

import com.google.android.gms.maps.model.LatLng

sealed class WeatherArea {
    abstract val polygon: List<LatLng>
    abstract val jsonKey: String
    abstract val localisedAreaName: String

    data class North(
        override val polygon: List<LatLng> = north,
        override val jsonKey: String = "north",
        override val localisedAreaName: String = "Noord"
    ) : WeatherArea()

    data class NorthWest(
        override val polygon: List<LatLng> = northWest,
        override val jsonKey: String = "north_west",
        override val localisedAreaName: String = "Noord-West"
    ) : WeatherArea()

    data class East(
        override val polygon: List<LatLng> = east,
        override val jsonKey: String = "east",
        override val localisedAreaName: String = "Oost"
    ) : WeatherArea()

    data class Middle(
        override val polygon: List<LatLng> = middle,
        override val jsonKey: String = "mid",
        override val localisedAreaName: String = "Midden"
    ) : WeatherArea()

    data class SouthWest(
        override val polygon: List<LatLng> = southWest,
        override val jsonKey: String = "south_west",
        override val localisedAreaName: String = "Zuid-West"
    ) : WeatherArea()

    data class SouthEast(
        override val polygon: List<LatLng> = southEast,
        override val jsonKey: String = "south_east",
        override val localisedAreaName: String = "Zuid-Oost"
    ) : WeatherArea()

}