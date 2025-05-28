package com.attijariwafabank.devisesapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.activities.LoginActivity
import com.attijariwafabank.devisesapp.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private  var _binding: FragmentSettingsBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        _binding?.languagesButton?.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_languages)
        }
        _binding?.themeButton?.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_themeFragment)
        }
        _binding?.profileButton?.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_profile)
        }
        _binding?.LogoutButton?.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), getString(R.string.logged_out), Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
        _binding?.backButton?.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_mainPage)
        }

    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()

    }

}
