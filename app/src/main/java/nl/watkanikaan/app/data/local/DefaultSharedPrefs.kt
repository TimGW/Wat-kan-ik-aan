package nl.watkanikaan.app.data.local

import com.google.android.gms.maps.model.LatLng
import nl.watkanikaan.app.domain.model.Profile
import javax.inject.Inject


class DefaultSharedPrefs @Inject constructor(
    private val spm: SharedPrefManager,
): SharedPref  {
    override fun setLocationSetting(latitude: Double, longitude: Double) {
        spm.setStringValue(SHARED_PREF_LOCATION_LAT, latitude.toString())
        spm.setStringValue(SHARED_PREF_LOCATION_LONG, longitude.toString())
    }

    override fun getLocationSetting(): LatLng? {
        val lat = spm.getStringValue(SHARED_PREF_LOCATION_LAT)?.toDoubleOrNull()
        val long = spm.getStringValue(SHARED_PREF_LOCATION_LONG)?.toDoubleOrNull()
        return if(lat != null && long != null) LatLng(lat, long) else null
    }

    override fun setDarkModeSetting(darkMode: Int) {
        spm.setIntValue(SHARED_PREF_DARK_MODE, darkMode)
    }

    override fun getDarkModeSetting() = spm.getIntValue(SHARED_PREF_DARK_MODE)

    override fun setThemeSetting(theme: Int) {
        spm.setIntValue(SHARED_PREF_THEME, theme)
    }

    override fun getThemeSetting(): Int = spm.getIntValue(SHARED_PREF_THEME)

    override fun getThermoception() = spm.getStringValue(SHARED_PREF_THERMOCEPTION)?.toInt() ?: -1

    override fun getProfile() = Profile(
        thermoception = when (getThermoception()) {
            0 -> Profile.Thermoception.Cold
            2 -> Profile.Thermoception.Warm
            else -> Profile.Thermoception.Normal
        },
    )

    companion object {
        const val SHARED_PREF_LOCATION_LAT = "SHARED_PREF_LOCATION_LAT"
        const val SHARED_PREF_LOCATION_LONG = "SHARED_PREF_LOCATION_LONG"
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_MODE"
        const val SHARED_PREF_THEME = "SHARED_PREF_THEME"

        const val SHARED_PREF_THERMOCEPTION = "profile_thermoception"
    }
}
