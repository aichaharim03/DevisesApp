package com.attijariwafabank.devisesapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

        private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        setupUI()


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

                        // Navigate to MainLogicActivity
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


        binding.textViewBacktoSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }



    }}