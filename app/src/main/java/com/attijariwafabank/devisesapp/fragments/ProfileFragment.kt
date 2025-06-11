package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private var binding : FragmentProfileBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            this.binding?.tvUserName?.text = currentUser.displayName ?: getString(R.string.not_available)
            this.binding?.tvUserEmail?.text = currentUser.email ?: getString(R.string.not_available)
            this.binding?.tvUserPhone?.text = currentUser.phoneNumber ?: getString(R.string.not_available)
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        this.binding?.changePasswordLayout?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_password)
        }

        this.binding?.editProfileLayout?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}
