package nl.watkanikaan.app.data.local

import nl.watkanikaan.app.domain.model.Profile

interface SharedPref {
    fun setLocationSetting(latitude: Double, longitude: Double)
    fun getLocationSetting(): String?
    fun setDarkModeSetting(darkMode: Int)
    fun getDarkModeSetting(): Int
    fun setThemeSetting(darkMode: Int)
    fun getThemeSetting(): Int?
    fun getThermoception(): Int
    fun getGender(): Int
    fun getAge(): Int?
    fun getProfile(): Profile
}
