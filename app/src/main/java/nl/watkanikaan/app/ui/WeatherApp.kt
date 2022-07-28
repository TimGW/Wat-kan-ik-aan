package nl.watkanikaan.app.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import nl.watkanikaan.app.data.local.SharedPrefs
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp : Application() {
    @Inject
    lateinit var sharedPref: SharedPrefs

    override fun onCreate() {
        super.onCreate()

        val nightMode = when (sharedPref.getDarkModeSetting()) {
            0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
