package com.attijariwafabank.devisesapp.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentLanguagesBinding
import com.attijariwafabank.devisesapp.databinding.FragmentLoginBinding
import com.attijariwafabank.devisesapp.databinding.FragmentPopUpBinding
import java.util.Locale


class PopUpFragment : DialogFragment() {

    private lateinit var binding: FragmentPopUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopUpBinding.inflate(inflater, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.butPopUpFr.setOnClickListener {
            changeLanguage("fr")
        }
        binding.butPopUpEng.setOnClickListener {
            changeLanguage("eng")
        }

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,  // Width
            ViewGroup.LayoutParams.WRAP_CONTENT   // Height
        )
        dialog?.window?.setBackgroundDrawableResource(R.color.ShearBlack2)
    }

    private fun changeLanguage(language: String) {
        LanguagesFragment.selectedLanguage = language
        val context = requireContext()
        val locale = Locale(LanguagesFragment.selectedLanguage)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Restart current activity to apply new language
        activity?.recreate()
    }

}



