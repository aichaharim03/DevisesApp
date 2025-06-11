package com.attijariwafabank.devisesapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.attijariwafabank.devisesapp.databinding.FragmentHelpBinding
class HelpFragment : Fragment() {

    private var binding: FragmentHelpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {


        binding?.btnCall?.setOnClickListener {
            makePhoneCall()
        }

        binding?.tvPhoneNumber?.setOnClickListener {
            makePhoneCall()
        }
    }

    private fun makePhoneCall() {
        val phoneNumber = binding?.tvPhoneNumber?.text.toString()
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}