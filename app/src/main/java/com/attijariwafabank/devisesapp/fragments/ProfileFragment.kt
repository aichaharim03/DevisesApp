package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.attijariwafabank.devisesapp.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.attijariwafabank.devisesapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return _binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        currentUser?.email?.let {
            _binding?.emailTextView?.text  = getString(R.string.email)
        }

        _binding?.changePasswordButton?.setOnClickListener {
            val oldPassword = _binding!!.oldPasswordEditText.text.toString()
            val newPassword = _binding!!.newPasswordEditText.text.toString()
            val confirmPassword = _binding!!.confirmNewPasswordEditText.text.toString()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_empty_field), Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), getString(R.string.error_passwords_notmatching), Toast.LENGTH_SHORT).show()
            } else {
                changePassword(oldPassword, newPassword)
            }
        }


    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val user = currentUser ?: return

        val credentials = EmailAuthProvider.getCredential(user.email!!, oldPassword)

        user.reauthenticate(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()

    }
}
