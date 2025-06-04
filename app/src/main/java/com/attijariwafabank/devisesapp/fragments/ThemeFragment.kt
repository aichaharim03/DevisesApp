package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
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

        binding?.radioDarkMode?.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding?.radioLightMode?.isChecked = false
            binding?.switchAutomatic?.isChecked = false

            }
        binding?.radioLightMode?.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding?.radioDarkMode?.isChecked = false
            binding?.switchAutomatic?.isChecked = false
        }

        binding?.switchAutomatic?.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                binding?.radioDarkMode?.isChecked = true
                binding?.radioLightMode?.isChecked = false
            }else{
                binding?.radioDarkMode?.isChecked = false
                binding?.radioLightMode?.isChecked = true
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}