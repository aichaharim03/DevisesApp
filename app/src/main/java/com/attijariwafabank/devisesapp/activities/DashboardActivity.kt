package com.attijariwafabank.devisesapp.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.ActivityDashboardBinding
import com.attijariwafabank.devisesapp.utils.ThemeUtils
import com.attijariwafabank.devisesapp.viewModels.CurrencyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: CurrencyViewModel by viewModels()
    private var isNavigationSetup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.loadTheme(this))
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_containerView) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainPage, R.id.agencyFragment2, R.id.Menu)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val bottomNavView: BottomNavigationView = binding.navigationView
        NavigationUI.setupWithNavController(bottomNavView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val localizedTitle = when (destination.id) {
                R.id.mainPage -> getString(R.string.home)
                R.id.agencyFragment2 -> getString(R.string.agencies)
                R.id.Menu -> getString(R.string.settings)
                R.id.currencyGraphFragment-> getString(R.string.line_chart)
                R.id.conversionFragment -> getString(R.string.conversion)
                R.id.settings -> getString(R.string.settings)
                R.id.profileFragment -> getString(R.string.profile)
                R.id.editProfileFragment -> getString(R.string.edit_profile)
                R.id.password -> getString(R.string.password)
                R.id.themeFragment -> getString(R.string.theme)
                R.id.languages -> getString(R.string.languages)
                R.id.helpFragment -> getString(R.string.help)


                else -> ""
            }
            supportActionBar?.title = localizedTitle

            if (isNavigationSetup) {
                when (destination.id) {
                    R.id.mainPage -> bottomNavView.selectedItemId = R.id.bottom_home
                    R.id.agencyFragment2 -> bottomNavView.selectedItemId = R.id.bottom_agency
                    R.id.Menu -> bottomNavView.selectedItemId = R.id.bottom_menu
                }
            }
        }

        bottomNavView.setOnItemSelectedListener { item ->
            if (isNavigationSetup) {
                when (item.itemId) {
                    R.id.bottom_home -> if (navController.currentDestination?.id != R.id.mainPage) {
                        navController.navigate(R.id.mainPage)
                    }
                    R.id.bottom_agency -> if (navController.currentDestination?.id != R.id.agencyFragment2) {
                        navController.navigate(R.id.agencyFragment2)
                    }
                    R.id.bottom_menu -> if (navController.currentDestination?.id != R.id.Menu) {
                        navController.navigate(R.id.Menu)
                    }
                }
            }
            true
        }

        auth = FirebaseAuth.getInstance()

        viewModel.errorLiveData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        isNavigationSetup = true

        if (savedInstanceState == null && navController.currentDestination?.id == null) {
            bottomNavView.selectedItemId = R.id.bottom_home
            navController.navigate(R.id.mainPage)
        } else {
            navController.currentDestination?.let {
                when (it.id) {
                    R.id.mainPage -> bottomNavView.selectedItemId = R.id.bottom_home
                    R.id.agencyFragment2 -> bottomNavView.selectedItemId = R.id.bottom_agency
                    R.id.Menu -> bottomNavView.selectedItemId = R.id.bottom_menu
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        isNavigationSetup = false
    }

    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences = newBase.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "en") ?: "en"

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
