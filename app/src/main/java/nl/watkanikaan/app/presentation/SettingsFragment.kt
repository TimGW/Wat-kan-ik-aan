package nl.watkanikaan.app.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import nl.watkanikaan.app.R
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.presentation.theme.ThemeHelper
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private var rateAppPref: Preference? = null
    private var darkModePref: ListPreference? = null
    private var profileThermoceptionPref: ListPreference? = null
    private var themePref: ListPreference? = null

    @Inject
    lateinit var sharedPrefs: SharedPref

    @Inject
    lateinit var themeHelper: ThemeHelper

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileThermoceptionPref = (findPreference("profile_thermoception") as? ListPreference)
        darkModePref = (findPreference("dark_mode_key") as? ListPreference)
        rateAppPref = (findPreference("preferences_rate_app_key") as? Preference)
        themePref = (findPreference("theme_key") as? ListPreference)

        profilePrefs()
        displayPrefs()
        aboutPrefs()
    }

    private fun profilePrefs() {
        profileThermoceptionPref?.summary = resources
            .getStringArray(R.array.thermoception_items)[sharedPrefs.getThermoception()]
        profileThermoceptionPref?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val setting = (newValue as String)
                    .toIntOrNull() ?: return@OnPreferenceChangeListener false
                profileThermoceptionPref?.summary =
                    resources.getStringArray(R.array.thermoception_items)[setting]
                true
            }
    }

    private fun displayPrefs() {
        darkModePref?.summary = resources
            .getStringArray(R.array.night_mode_items)[sharedPrefs.getDarkModeSetting()]
        darkModePref?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val value = (newValue as String).toIntOrNull() ?: return@OnPreferenceChangeListener false
                AppCompatDelegate.setDefaultNightMode(themeHelper.getNightMode(value))
                sharedPrefs.setDarkModeSetting(value)
                darkModePref?.summary = resources.getStringArray(R.array.night_mode_items)[value]
                true
            }


        if (themeHelper.hasDynamicColors()) {
            val themeItems = resources.getStringArray(R.array.theme_items)
            themePref?.entries = themeItems
            themePref?.entryValues = resources.getStringArray(R.array.theme_values)
            themePref?.summary = themeItems[sharedPrefs.getThemeSetting() ?: 0]
        } else {
            val themeItems = resources.getStringArray(R.array.legacy_theme_items)
            themePref?.entries = themeItems
            themePref?.entryValues = resources.getStringArray(R.array.legacy_theme_values)
            themePref?.summary = themeItems[sharedPrefs.getThemeSetting() ?: 0]
        }

        themePref?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val themeSetting = (newValue as String).toIntOrNull() ?: return@OnPreferenceChangeListener false
                sharedPrefs.setThemeSetting(themeSetting)
                requireActivity().recreate()
                true
            }
    }

    private fun aboutPrefs() {
        rateAppPref?.setOnPreferenceClickListener {
            val manager = ReviewManagerFactory.create(requireActivity())

            manager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        requireContext().toast(getString(R.string.review_flow_done))
                    }
                } else {
                    requireContext().toast(getString(R.string.error_generic))
                }
            }
            true
        }
    }
}