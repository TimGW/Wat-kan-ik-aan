package nl.watkanikaan.app.presentation.theme

import nl.watkanikaan.app.presentation.theme.ThemeHelperImpl.Companion.SAVED_NIGHT_VALUE

interface ThemeHelper {
    fun getAppTheme(): Int?
    fun getNightMode(darkModeSetting: Int = SAVED_NIGHT_VALUE): Int
    fun applyDynamicColors()
    fun hasDynamicColors(): Boolean
    fun getNavBarFlags(isNight: Boolean): Int
}