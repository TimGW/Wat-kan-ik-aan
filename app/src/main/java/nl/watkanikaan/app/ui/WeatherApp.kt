package nl.watkanikaan.app.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp : Application() {
    @Inject
    lateinit var themeHelper: ThemeHelper

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(themeHelper.getNightMode())
    }
}
