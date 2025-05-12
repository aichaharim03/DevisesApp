package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentMainPageBinding
import com.attijariwafabank.devisesapp.databinding.FragmentThemeBinding


class ThemeFragment : Fragment() {

    private var binding: FragmentThemeBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThemeBinding.inflate(inflater, container, false)
        return binding!!.root    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.backButton?.setOnClickListener {
            findNavController().navigate(R.id.action_themeFragment_to_settings)
        }


    }


}