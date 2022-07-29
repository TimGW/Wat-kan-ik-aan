package nl.watkanikaan.app.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import nl.watkanikaan.app.R
import nl.watkanikaan.app.databinding.ActivityMainBinding
import nl.watkanikaan.app.domain.model.Weather
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()

    @Inject
    lateinit var themeHelper: ThemeHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(themeHelper.getAppTheme())

        setSupportActionBar(binding.appbar.toolbar)
        setToolbarTitle(R.string.now)

        NavigationUI.setupActionBarWithNavController(
            this,
            getNavController(),
            AppBarConfiguration.Builder(R.id.weatherFragment).build()
        )

        launchAfter {
            viewModel.toolbar.collect { titleRes ->
                setToolbarTitle(titleRes)
            }
        }
    }

    private fun setToolbarTitle(@StringRes titleRes: Int?) {
        if (titleRes == null) return

        val day = getString(titleRes)
        val title = getString(R.string.app_name_param, day.lowercase(Locale.getDefault()))
        supportActionBar?.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        return getNavController().navigateUp() || super.onSupportNavigateUp()
    }

    private fun getNavController(): NavController {
        return if (navController == null) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            (navHostFragment as NavHostFragment).navController
        } else {
            navController as NavController
        }
    }
}