package com.example.devises

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

        binding.butLogIn.setOnClickListener {
            val email=binding.LogInEmail.text.toString()
            val password=binding.LoginInPassword.text.toString()

            if( email.isNotEmpty() && password.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
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