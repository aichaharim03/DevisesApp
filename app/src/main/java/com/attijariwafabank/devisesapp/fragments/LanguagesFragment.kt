package com.attijariwafabank.devisesapp.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.attijariwafabank.devisesapp.databinding.FragmentLanguagesBinding
import java.util.Locale

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

        binding.butEng.setOnClickListener {
            changeLanguage("en")
        }

        binding.butFr.setOnClickListener {
            changeLanguage("fr")
        }
    }

    private fun changeLanguage(language: String) {
        selectedLanguage = language
        val context = requireContext()
        val locale = Locale(selectedLanguage)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Restart current activity to apply new language
        activity?.recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
