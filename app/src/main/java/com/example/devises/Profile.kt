package com.example.devises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.EmailAuthProvider
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController


class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var emailTextView: TextView
    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmNewPasswordEditText: EditText
    private lateinit var changePasswordButton: AppCompatButton
    private lateinit var logoutButton: AppCompatButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        // Initialize views using findViewById
        emailTextView = view.findViewById(R.id.emailTextView)
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText)
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText)
        confirmNewPasswordEditText = view.findViewById(R.id.confirmNewPasswordEditText)
        changePasswordButton = view.findViewById(R.id.changePasswordButton)


        // Set the email text if the user is logged in
        currentUser?.email?.let { email ->
            emailTextView.text = "Email: $email"
        }

        // Handle the password change button click
        changePasswordButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmNewPasswordEditText.text.toString()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                changePassword(oldPassword, newPassword)
            }
        }
        logoutButton = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Logout the user
            auth.signOut()

            // Show a message
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            // Navigate to the LoginFragment
            findNavController().navigate(R.id.action_profile_to_welcomeFragment)
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val user = currentUser ?: return

        // Re-authenticate the user with their old password
        val credentials = EmailAuthProvider.getCredential(user.email!!, oldPassword)

        user.reauthenticate(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Once re-authenticated, change the password
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // If re-authentication failed, show error message
                    Toast.makeText(requireContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
