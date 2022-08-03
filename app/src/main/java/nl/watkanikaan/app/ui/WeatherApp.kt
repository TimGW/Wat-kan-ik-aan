package nl.watkanikaan.app.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
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
