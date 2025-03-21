package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.findNavController
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentWelcomeBinding
import java.util.Locale

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.butWelcomeLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
        binding.butWelcomeSignup.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_signupFragment)
        }
        binding.languageButton.setOnClickListener {
            val showPopUp = PopUpFragment()
            showPopUp.show(childFragmentManager,"PopUpFragment")
        }
    }

}


