package nl.watkanikaan.app.data.local

import com.google.android.gms.maps.model.LatLng
import nl.watkanikaan.app.domain.model.Profile

interface SharedPref {
    fun setLocationSetting(latitude: Double, longitude: Double)
    fun getLocationSetting(): LatLng?
    fun setDarkModeSetting(darkMode: Int)
    fun getDarkModeSetting(): Int
    fun setThemeSetting(darkMode: Int)
    fun getThemeSetting(): Int?
    fun getThermoception(): Int
    fun getGender(): Int
    fun getAge(): Int?
    fun getProfile(): Profile
}
