package nl.watkanikaan.app.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import nl.watkanikaan.app.presentation.theme.ThemeHelper
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp : Application() {
    @Inject
    lateinit var themeHelper: ThemeHelper

    override fun onCreate() {
        super.onCreate()

        themeHelper.applyDynamicColors()

        AppCompatDelegate.setDefaultNightMode(themeHelper.getNightMode())
    }
}
