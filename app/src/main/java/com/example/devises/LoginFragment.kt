package com.example.devises

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.devises.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(inflater,container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("userEmail", "")

        if (!savedEmail.isNullOrEmpty()) {
            binding.LogInEmail.setText(savedEmail) // Set email in the email field
        }

        binding.butLogIn.setOnClickListener {
            val email=binding.LogInEmail.text.toString()
            val password=binding.LoginInPassword.text.toString()

            if( email.isNotEmpty() && password.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val editor = sharedPreferences.edit()
                        editor.putString("userEmail", email)
                        editor.apply()
                        findNavController().navigate(R.id.action_loginFragment_to_mainPage)
                    }else{Toast.makeText(requireContext(),it.exception.toString(), Toast.LENGTH_SHORT).show()}
                }
                }else { Toast.makeText(requireContext(),"Fields cannot be empty", Toast.LENGTH_SHORT).show()}

        }



        binding.textViewBacktoSignUp.setOnClickListener{
            it.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        return binding.root
    }

}