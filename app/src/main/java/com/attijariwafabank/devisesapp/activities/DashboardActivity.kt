package com.attijariwafabank.devisesapp.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.attijariwafabank.devisesapp.CurrencyViewModel
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView
import java.util.Locale
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.attijariwafabank.devisesapp.Currency
import com.attijariwafabank.devisesapp.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private val viewModel: CurrencyViewModel by viewModels()
    private var currencyResponse: Currency? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        setContentView(binding.root)

        viewModel.fetchCurrencies("6f0f2b6daaabd35b2281bd363bde71e5")

        viewModel.currenciesLiveData.observe(this) { currencies ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fromCurrencySpinner.adapter = adapter
            binding.toCurrencySpinner.adapter = adapter
        }

        viewModel.errorLiveData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }


        binding.convertButton.setOnClickListener {
            val accessKey = "6f0f2b6daaabd35b2281bd363bde71e5"
            val fromCurrency = binding.fromCurrencySpinner.selectedItem?.toString()
            val toCurrency = binding.toCurrencySpinner.selectedItem?.toString()
            val amount = binding.amountEditText.text.toString().toDoubleOrNull()

            if (amount != null && amount > 0 && fromCurrency != null && toCurrency != null) {
                viewModel.convertCurrency(accessKey, fromCurrency, toCurrency, amount)
            } else {
                Toast.makeText(this, "Please enter valid data", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.conversionResult.observe(this) { result ->
            binding.resultTextView.text = "$result"
        }

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)

        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_containerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
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

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        navController = findNavController(R.id.nav_host_fragment_containerView)
        when (p0.itemId) {
            R.id.nav_main_page -> navController.navigate(R.id.mainPage)
            R.id.nav_profile -> navController.navigate(R.id.profile)
            R.id.nav_settings -> navController.navigate(R.id.settings)
        }
        drawerLayout.closeDrawer(navigationView)
        return true
    }
}
