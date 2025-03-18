package com.example.devises

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.devises.databinding.FragmentMainPageBinding
import com.example.devises.databinding.FragmentWelcomeBinding
import com.google.firebase.auth.FirebaseAuth


class MainPage : Fragment() {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var logoutButton: AppCompatButton
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainPageBinding.inflate(inflater, container, false)

        binding.butProfile.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainPage_to_profile)
        }
        binding.butSettings.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainPage_to_settings)
        }


        return binding.root
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
            findNavController().navigate(R.id.action_mainPage_to_welcomeFragment)
        }


    }
}