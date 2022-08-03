package nl.watkanikaan.app.ui

import android.content.res.Resources
import android.view.View
import nl.watkanikaan.app.ui.ThemeHelperImpl.Companion.SAVED_NIGHT_VALUE

interface ThemeHelper {
    fun getAppTheme(): Int?
    fun getNightMode(darkModeSetting: Int = SAVED_NIGHT_VALUE): Int
    fun applyDynamicColors()
    fun hasDynamicColors(): Boolean
    fun setLegacyNavBarColor(decorView: View, resources: Resources)
}