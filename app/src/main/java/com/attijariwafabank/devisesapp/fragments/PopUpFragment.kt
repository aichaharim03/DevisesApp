package com.attijariwafabank.devisesapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentPopUpBinding


class PopUpFragment : DialogFragment() {

    private var _binding: FragmentPopUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopUpBinding.inflate(inflater, container, false)

        return _binding!!.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.butPopUpFr?.setOnClickListener {
            changeLanguage("fr")
        }
        _binding?.butPopUpEng?.setOnClickListener {
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

        val sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.settings), Context.MODE_PRIVATE)
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



