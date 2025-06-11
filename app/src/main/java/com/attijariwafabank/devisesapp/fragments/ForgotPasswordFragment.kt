package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.attijariwafabank.devisesapp.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment()  {

    private var binding : FragmentForgotPasswordBinding? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private var resendTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        setupClickListeners()
        setupUI()
    }

    private fun setupClickListeners() {
        binding?.apply {
            // Send reset link button
            btnSendResetLink.setOnClickListener {
                sendPasswordResetEmail()
            }

            // Back to login button - Fixed navigation
            btnBackToLogin.setOnClickListener {
                // Use parentFragmentManager to go back to the previous screen
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupUI() {
        // Hide success message and error initially
        binding?.clSuccessMessage?.visibility = View.GONE
        binding?.tvErrorMessage?.visibility = View.GONE

        // Set up email input validation
        binding?.etEmail?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
    }

    private fun sendPasswordResetEmail() {
        val email = binding?.etEmail?.text.toString().trim()

        if (!validateEmail()) {
            return
        }

        showLoading(true)
        clearErrorMessages()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    showSuccessMessage("Password reset email sent to $email")
                    binding?.etEmail?.isEnabled = false
                } else {
                    val errorMessage = when {
                        task.exception?.message?.contains("user") == true ->
                            "No account found with this email address"
                        task.exception?.message?.contains("network") == true ->
                            "Network error. Please check your connection"
                        else ->
                            task.exception?.message ?: "Failed to send reset email"
                    }
                    showError(errorMessage)
                }
            }
    }

    private fun validateEmail(): Boolean {
        val email = binding?.etEmail?.text.toString().trim()

        return when {
            email.isEmpty() -> {
                showError("Email is required")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Please enter a valid email address")
                false
            }
            else -> {
                clearErrorMessages()
                true
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding?.apply {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            btnSendResetLink.isEnabled = !show
            btnSendResetLink.text = if (show) "Sending..." else "Send Reset Link"
        }
    }

    private fun showSuccessMessage(message: String) {
        binding?.apply {
            clSuccessMessage.visibility = View.VISIBLE
            tvSuccessMessage.text = message

            // Hide after 5 seconds
            clSuccessMessage.postDelayed({
                clSuccessMessage.visibility = View.GONE
            }, 5000)
        }
    }

    private fun showError(message: String) {
        binding?.apply {
            tvErrorMessage.text = message
            tvErrorMessage.visibility = View.VISIBLE
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun clearErrorMessages() {
        binding?.apply {
            tvErrorMessage.visibility = View.GONE
            clSuccessMessage.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resendTimer?.cancel()
        binding = null
    }
}