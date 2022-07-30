package nl.watkanikaan.app.ui

import androidx.appcompat.app.AppCompatDelegate
import nl.watkanikaan.app.R
import nl.watkanikaan.app.data.local.SharedPrefs
import javax.inject.Inject

class ThemeHelperImpl @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : ThemeHelper {

    override fun getAppTheme() = when (sharedPrefs.getThemeSetting()) {
        0 -> R.style.AppTheme_Yellow
        1 -> R.style.AppTheme_Blue
        2 -> R.style.AppTheme_Red
        3 -> R.style.AppTheme_Green
        else -> R.style.AppTheme_Yellow
    }

    override fun getNightMode(darkModeSetting: Int): Int {
        val result = if (darkModeSetting == SAVED_NIGHT_VALUE) {
            sharedPrefs.getDarkModeSetting()
        } else {
            darkModeSetting
        }

        return when (result) {
            0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
        }
    }

    companion object {
        const val SAVED_NIGHT_VALUE = 1234
    }
}