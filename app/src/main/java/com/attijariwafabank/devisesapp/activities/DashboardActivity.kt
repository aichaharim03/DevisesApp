package com.attijariwafabank.devisesapp.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.attijariwafabank.devisesapp.CurrencyViewModel
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView
import java.util.Locale
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import com.attijariwafabank.devisesapp.databinding.NavHeaderBinding

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView


        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        viewModel.fetchCurrencies(
            "ca153fc53a18d844476abcc90b57143c",
            "MAD",
            "USD,EUR,GBP,CAD,MAD,AUD,JPY,CHF,CNY,SEK,NZD,INR,MLR"
        )

        binding.navigationView.setNavigationItemSelectedListener(this)


    }

    override fun onStart() {
        super.onStart()

        navController = Navigation.findNavController(binding.navHostFragmentContainerView)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("userEmail", "")

        viewModel.errorLiveData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        val emailMenuItem = binding.navigationView.menu.findItem(R.id.userEmail)
        emailMenuItem.title = savedEmail ?: "No Email Found"
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_main_page -> navController.navigate(R.id.mainPage)
            R.id.nav_profile -> navController.navigate(R.id.profile)
            R.id.nav_settings -> navController.navigate(R.id.settings)
        }
        drawerLayout.closeDrawer(navigationView)
        return true
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
