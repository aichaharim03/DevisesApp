package com.attijariwafabank.devisesapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentLanguagesBinding

class LanguagesFragment : Fragment() {

    private var _binding: FragmentLanguagesBinding? = null
    private val binding get() = _binding!!

    companion object {
        var selectedLanguage: String = "en"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.englishButton.setOnClickListener {
            changeLanguage("en")
        }

        binding.frenchButton.setOnClickListener {
            changeLanguage("fr")
        }
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_languages_to_settings)
        }
    }

    private fun changeLanguage(language: String) {
        selectedLanguage = language

        val sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("My_Lang", language)
            apply()
        }

        requireActivity().recreate()
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()

    }
}
