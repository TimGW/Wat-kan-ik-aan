package nl.watkanikaan.app.data.local

import javax.inject.Inject


class SharedPrefs @Inject constructor(
    private val spm: SharedPrefManager,
) {
    fun setDarkModeSetting(darkMode: Int) {
        spm.setIntValue(SHARED_PREF_DARK_MODE, darkMode)
    }

    fun getDarkModeSetting() = spm.getIntValue(SHARED_PREF_DARK_MODE)

    fun getThermoception() = spm.getStringValue(SHARED_PREF_THERMOCEPTION)?.toInt() ?: 0

    fun getGender() = spm.getStringValue(SHARED_PREF_GENDER)?.toInt() ?: 0

    fun getAge() = spm.getStringValue(SHARED_PREF_AGE)?.toInt() ?: 0

    companion object {
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_MODE"
        const val SHARED_PREF_THERMOCEPTION = "profile_thermoception"
        const val SHARED_PREF_GENDER = "profile_gender"
        const val SHARED_PREF_AGE = "profile_age"
    }
}
