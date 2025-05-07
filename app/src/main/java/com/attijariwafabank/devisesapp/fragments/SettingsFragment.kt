package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.R
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

        _binding?.LanguagesButton?.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_languages)
        }

    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()

    }

}
