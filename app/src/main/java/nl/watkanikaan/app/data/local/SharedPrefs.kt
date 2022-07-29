package nl.watkanikaan.app.data.local

import nl.watkanikaan.app.domain.model.Profile
import javax.inject.Inject


class SharedPrefs @Inject constructor(
    private val spm: SharedPrefManager,
) {
    fun setDarkModeSetting(darkMode: Int) {
        spm.setIntValue(SHARED_PREF_DARK_MODE, darkMode)
    }

    fun getDarkModeSetting() = spm.getIntValue(SHARED_PREF_DARK_MODE)

    fun setThemeSetting(darkMode: Int) {
        spm.setIntValue(SHARED_PREF_THEME, darkMode)
    }

    fun getThemeSetting() = spm.getIntValue(SHARED_PREF_THEME)

    fun getThermoception() = spm.getStringValue(SHARED_PREF_THERMOCEPTION)?.toInt() ?: -1

    fun getGender() = spm.getStringValue(SHARED_PREF_GENDER)?.toInt() ?: 0

    fun getAge(): Int? = spm.getStringValue(SHARED_PREF_AGE)?.toInt()

    fun getProfile() = Profile(
        thermoception = when (getThermoception()) {
            0 -> Profile.Thermoception.Cold
            2 -> Profile.Thermoception.Warm
            else -> Profile.Thermoception.Normal
        },
        gender = when (getGender()) {
            1 -> Profile.Gender.Man
            2 -> Profile.Gender.Woman
            else -> Profile.Gender.Unspecified
        },
        age = getAge() ?: 0,
    )

    companion object {
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_MODE"
        const val SHARED_PREF_THEME = "SHARED_PREF_THEME"

        const val SHARED_PREF_THERMOCEPTION = "profile_thermoception"
        const val SHARED_PREF_GENDER = "profile_gender"
        const val SHARED_PREF_AGE = "profile_age"
    }
}
