package nl.watkanikaan.app.data.local

import javax.inject.Inject


class SharedPrefs @Inject constructor(
    private val spm: SharedPrefManager,
) {
    fun setDarkModeSetting(darkMode: Int) {
        spm.setIntValue(SHARED_PREF_DARK_MODE, darkMode)
    }

    fun getDarkModeSetting() = spm.getIntValue(SHARED_PREF_DARK_MODE)

    companion object {
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_MODE"
    }
}
