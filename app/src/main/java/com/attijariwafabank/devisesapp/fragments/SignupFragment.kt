package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.databinding.FragmentSignupBinding
import com.attijariwafabank.devisesapp.R
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.butSignup.setOnClickListener {
            handleSignup()
        }

        binding.textBacktoLogin.setOnClickListener {
            it.findNavController()?.navigate(R.id.action_signupFragment_to_loginFragment2)
        }
    }

    private fun handleSignup() {

        val email = binding.signupEmail.text.toString()
        val password = binding.signupPassword.text.toString()
        val confirmPassword = binding.signupConfirmPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        findNavController().navigate(R.id.action_signupFragment_to_loginFragment2)
                    } else {
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

}
