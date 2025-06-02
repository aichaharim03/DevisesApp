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
import com.attijariwafabank.devisesapp.databinding.FragmentMenuBinding
import com.google.firebase.auth.FirebaseAuth


class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()


        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_mainPage)
        }

        binding.navConversion.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_conversionFragment)

        }

        binding.navAgency.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_agencyFragment2)

        }

        binding.navHelp.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_languages)

        }

        binding.svSettings.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_settings)

        }

        binding.navLogout.setOnClickListener {

            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
