package nl.watkanikaan.app.data.local

import nl.watkanikaan.app.domain.model.Profile
import javax.inject.Inject


class DefaultSharedPrefs @Inject constructor(
    private val spm: SharedPrefManager,
): SharedPref  {
    override fun setLocationSetting(latitude: Double, longitude: Double) {
        spm.setStringValue(SHARED_PREF_LOCATION, "$latitude,$longitude")
    }

    override fun getLocationSetting() = spm.getStringValue(SHARED_PREF_LOCATION)

    override fun setDarkModeSetting(darkMode: Int) {
        spm.setIntValue(SHARED_PREF_DARK_MODE, darkMode)
    }

    override fun getDarkModeSetting() = spm.getIntValue(SHARED_PREF_DARK_MODE)

    override fun setThemeSetting(darkMode: Int) {
        spm.setIntValue(SHARED_PREF_THEME, darkMode)
    }

    override fun getThemeSetting() = spm.getIntValue(SHARED_PREF_THEME)

    override fun getThermoception() = spm.getStringValue(SHARED_PREF_THERMOCEPTION)?.toInt() ?: -1

    override fun getGender() = spm.getStringValue(SHARED_PREF_GENDER)?.toInt() ?: 0

    override fun getAge(): Int? = spm.getStringValue(SHARED_PREF_AGE)?.toInt()

    override fun getProfile() = Profile(
        thermoception = when (getThermoception()) {
            0 -> Profile.Thermoception.Cold
            2 -> Profile.Thermoception.Warm
            else -> Profile.Thermoception.Normal
        },
        gender = when (getGender()) {
            1 -> Profile.Gender.Male
            2 -> Profile.Gender.Female
            else -> Profile.Gender.Unspecified
        },
        age = getAge() ?: 0,
    )

    companion object {
        const val SHARED_PREF_LOCATION = "SHARED_PREF_LOCATION"
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_MODE"
        const val SHARED_PREF_THEME = "SHARED_PREF_THEME"

        const val SHARED_PREF_THERMOCEPTION = "profile_thermoception"
        const val SHARED_PREF_GENDER = "profile_gender"
        const val SHARED_PREF_AGE = "profile_age"
    }
}
