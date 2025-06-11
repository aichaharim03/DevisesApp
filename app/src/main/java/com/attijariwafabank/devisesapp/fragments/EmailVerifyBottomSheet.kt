package com.attijariwafabank.devisesapp.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.attijariwafabank.devisesapp.activities.LoginActivity
import com.attijariwafabank.devisesapp.databinding.FragmentEmailVerifyBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth

class EmailVerifyBottomSheet : BottomSheetDialogFragment() {

    private var binding : FragmentEmailVerifyBottomSheetBinding? = null

    private var countdownHandler: Handler? = null
    private var countdownRunnable: Runnable? = null
    private var secondsRemaining = 5

    companion object {
        fun newInstance(): EmailVerifyBottomSheet {
            return EmailVerifyBottomSheet()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailVerifyBottomSheetBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        setupCountdown()
        startCountdown()
    }

    private fun setupCountdown() {
        countdownHandler = Handler(Looper.getMainLooper())
        countdownRunnable = object : Runnable {
            override fun run() {
                if (secondsRemaining > 0) {
                    updateCountdownText()
                    secondsRemaining--
                    countdownHandler?.postDelayed(this, 1000)
                } else {
                    // Time's up, sign out and redirect to login
                    signOutAndRedirect()
                }
            }
        }
    }

    private fun startCountdown() {
        countdownHandler?.post(countdownRunnable!!)
    }

    private fun updateCountdownText() {
        val message = "Verification email sent! Please verify your email.\n\nYou will be logged out in $secondsRemaining seconds."
        this.binding?.tvMessage?.text = message
    }

    private fun signOutAndRedirect() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownHandler?.removeCallbacks(countdownRunnable!!)
        countdownHandler = null
        countdownRunnable = null
        this.binding = null
    }
}