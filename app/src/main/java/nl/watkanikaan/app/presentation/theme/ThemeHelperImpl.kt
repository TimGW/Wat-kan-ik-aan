package nl.watkanikaan.app.presentation.theme

import android.app.Application
import android.content.Context
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.qualifiers.ApplicationContext
import nl.watkanikaan.app.R
import nl.watkanikaan.app.data.local.SharedPref
import javax.inject.Inject


class ThemeHelperImpl @Inject constructor(
    @ApplicationContext val application: Context,
    private val defaultSharedPrefs: SharedPref
) : ThemeHelper {

    override fun getAppTheme(): Int? {
        val theme = defaultSharedPrefs.getThemeSetting()
        return if (hasDynamicColors()) {
            when (theme) {
                0 -> null
                1 -> R.style.AppTheme_Yellow
                2 -> R.style.AppTheme_Blue
                3 -> R.style.AppTheme_Red
                4 -> R.style.AppTheme_Green
                else -> R.style.AppTheme_Yellow
            }
        } else {
            when (theme) {
                0 -> R.style.AppTheme_Yellow
                1 -> R.style.AppTheme_Blue
                2 -> R.style.AppTheme_Red
                3 -> R.style.AppTheme_Green
                else -> R.style.AppTheme_Yellow
            }
        }
    }

    override fun getNightMode(darkModeSetting: Int): Int {
        val result = if (darkModeSetting == SAVED_NIGHT_VALUE) {
            defaultSharedPrefs.getDarkModeSetting()
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

    override fun applyDynamicColors() {
        DynamicColors.applyToActivitiesIfAvailable(application as Application)
    }

    override fun hasDynamicColors() = DynamicColors.isDynamicColorAvailable()

    override fun getNavBarFlags(isNight: Boolean) = if (isNight) {
        FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or FLAG_TRANSLUCENT_NAVIGATION
    } else {
        FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }

    companion object {
        const val SAVED_NIGHT_VALUE = 1234
    }
}