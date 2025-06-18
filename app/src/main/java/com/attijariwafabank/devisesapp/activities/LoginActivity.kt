package com.attijariwafabank.devisesapp.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.ActivityLoginBinding
import com.attijariwafabank.devisesapp.fragments.ForgotPasswordFragment
import com.attijariwafabank.devisesapp.utils.ThemeUtils
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.loadTheme(this))

        binding = ActivityLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        setupUI()
        setupBackPressedHandler()
    }

    private fun setupUI() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("userEmail", "")

        if (!savedEmail.isNullOrEmpty()) {
            binding.LogInEmail.setText(savedEmail)
        }

        binding.butLogIn.setOnClickListener {
            val email = binding.LogInEmail.text.toString()
            val password = binding.LoginInPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val editor = sharedPreferences.edit()
                        editor.putString("userEmail", email)
                        editor.apply()

                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            it.exception?.message ?: "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.error_empty_field), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.ForgotPassword.setOnClickListener {
            showForgotPasswordFragment()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            binding.loginLayout.visibility = if (supportFragmentManager.backStackEntryCount == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun showForgotPasswordFragment() {
        binding.loginLayout.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ForgotPasswordFragment())
            .addToBackStack("forgot_password")
            .commit()
    }

    override fun onResume() {
        super.onResume()
        binding.loginLayout.visibility = if (supportFragmentManager.backStackEntryCount == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
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
}