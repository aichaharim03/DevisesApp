package com.example.devises

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class Settings : Fragment() {
    private lateinit var logoutButton: AppCompatButton
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton = view.findViewById(R.id.logoutButton)
        auth = FirebaseAuth.getInstance()
        logoutButton.setOnClickListener {
            // Logout the user
            auth.signOut()

            // Show a message
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            // Navigate to the LoginFragment
            findNavController().navigate(R.id.action_settings_to_welcomeFragment)
        }
}
}

