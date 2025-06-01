package com.attijariwafabank.devisesapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.activities.LoginActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import com.attijariwafabank.devisesapp.databinding.FragmentEditProfileBinding



class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var currentUser: FirebaseUser? = null

    private var isEditingName = false
    private var isEditingEmail = false
    private var isEditingPhone = false
    private var originalName = ""
    private var originalEmail = ""
    private var originalPhone = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        setupUserData()
        setupPhoneVerificationCallbacks()
        setupClickListeners()
    }

    private fun setupUserData() {
        currentUser?.let { user ->
            originalName = user.displayName ?: getString(R.string.not_set)
            originalEmail = user.email ?: getString(R.string.not_set)
            originalPhone = user.phoneNumber ?: getString(R.string.not_set)

            binding.tvName.text = originalName
            binding.tvEmail.text = originalEmail
            binding.tvPhone.text = originalPhone
        }
    }

    private fun setupClickListeners() {
        // Name
        binding.layoutName.setOnClickListener { toggleNameEdit() }
        binding.btnSaveName.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            if (newName.isNotEmpty()) updateName(newName)
            else showToast(getString(R.string.name_cannot_be_empty))
        }
        binding.btnCancelName.setOnClickListener { cancelNameEdit() }

        // Email
        binding.layoutEmail.setOnClickListener { toggleEmailEdit() }
        binding.btnSaveEmail.setOnClickListener {
            val newEmail = binding.etEmail.text.toString().trim()
            val password = binding.etPasswordForEmail.text.toString()
            if (newEmail.isNotEmpty() && password.isNotEmpty()) {
                if (newEmail != originalEmail)
                    updateEmail(newEmail, password)
                else showToast(getString(R.string.email_is_the_same))
            } else showToast(getString(R.string.please_fill_all_fields))
        }
        binding.btnCancelEmail.setOnClickListener { cancelEmailEdit() }

        // Phone
        binding.layoutPhone.setOnClickListener { togglePhoneEdit() }
        binding.btnSendOtp.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            if (phone.isNotEmpty()) sendOtp(phone)
            else showToast(getString(R.string.please_enter_phone_number))
        }
        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()
            if (otp.isNotEmpty()) verifyOtp(otp)
            else showToast(getString(R.string.please_enter_otp))
        }
        binding.btnCancelPhone.setOnClickListener { cancelPhoneEdit() }
    }

    private fun toggleNameEdit() {
        isEditingName = !isEditingName
        binding.etName.setText(originalName)
        binding.etName.visibility = if (isEditingName) View.VISIBLE else View.GONE
        binding.layoutNameActions.visibility = if (isEditingName) View.VISIBLE else View.GONE
    }

    private fun cancelNameEdit() {
        isEditingName = false
        binding.etName.setText("")
        binding.etName.visibility = View.GONE
        binding.layoutNameActions.visibility = View.GONE
    }

    private fun toggleEmailEdit() {
        isEditingEmail = !isEditingEmail
        binding.etEmail.setText(originalEmail)
        val visibility = if (isEditingEmail) View.VISIBLE else View.GONE
        binding.etEmail.visibility = visibility
        binding.etPasswordForEmail.visibility = visibility
        binding.layoutEmailActions.visibility = visibility
    }

    private fun cancelEmailEdit() {
        isEditingEmail = false
        binding.etEmail.setText("")
        binding.etPasswordForEmail.setText("")
        binding.etEmail.visibility = View.GONE
        binding.etPasswordForEmail.visibility = View.GONE
        binding.layoutEmailActions.visibility = View.GONE
    }

    private fun togglePhoneEdit() {
        isEditingPhone = !isEditingPhone
        binding.etPhone.setText(originalPhone)
        val visibility = if (isEditingPhone) View.VISIBLE else View.GONE
        binding.etPhone.visibility = visibility
        binding.btnSendOtp.visibility = visibility
        binding.layoutPhoneActions.visibility = visibility
    }

    private fun cancelPhoneEdit() {
        isEditingPhone = false
        binding.etPhone.setText("")
        binding.etOtp.setText("")
        binding.etPhone.visibility = View.GONE
        binding.etOtp.visibility = View.GONE
        binding.btnSendOtp.visibility = View.GONE
        binding.btnVerifyOtp.visibility = View.GONE
        binding.layoutPhoneActions.visibility = View.GONE
        verificationId = null
    }

    private fun updateName(name: String) {
        val updates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        currentUser?.updateProfile(updates)?.addOnCompleteListener {
            if (it.isSuccessful) {
                originalName = name
                binding.tvName.text = name
                cancelNameEdit()
                showToast(getString(R.string.name_updated_successfully))
            } else {
                showToast(getString(R.string.failed_to_update_name))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateEmail(newEmail: String, password: String) {
        val user = currentUser ?: return
        val currentEmail = user.email ?: return
        binding.btnSaveEmail.isEnabled = false
        binding.btnSaveEmail.text = getString(R.string.updating)

        val credential = EmailAuthProvider.getCredential(currentEmail, password)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.verifyBeforeUpdateEmail(newEmail)
                    .addOnSuccessListener {
                        showToast(getString(R.string.verification_email_sent_please_verify_ur_email_you_will_be_logged_out))
                        Handler(Looper.getMainLooper()).postDelayed({
                            FirebaseAuth.getInstance().signOut()
                            redirectToLogin()
                        }, 3000)
                    }
                    .addOnFailureListener { showError(
                        getString(
                            R.string.failed_to_send_verification,
                            it.message
                        )) }
            }
            .addOnFailureListener { showError(getString(R.string.authentication_failed, it.message)) }
            .addOnCompleteListener {
                binding.btnSaveEmail.isEnabled = true
                binding.btnSaveEmail.text = getString(R.string.save)
            }
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun checkEmailUpdateStatus() {
        currentUser?.reload()?.addOnCompleteListener {
            if (it.isSuccessful) {
                val updatedEmail = currentUser?.email
                if (!updatedEmail.isNullOrBlank() && updatedEmail != originalEmail) {
                    originalEmail = updatedEmail
                    binding.tvEmail.text = updatedEmail
                    showToast(getString(R.string.email_updated_successfully))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkEmailUpdateStatus()
    }

    private fun setupPhoneVerificationCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                currentUser?.updatePhoneNumber(credential)
                    ?.addOnSuccessListener {
                        showToast(getString(R.string.phone_number_updated_successfully))
                        originalPhone = binding.etPhone.text.toString().trim()
                        binding.tvPhone.text = originalPhone
                        cancelPhoneEdit()
                    }
                    ?.addOnFailureListener { showError(
                        getString(
                            R.string.failed_to_update_phone,
                            it.message
                        )) }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                showError(getString(R.string.verification_failed, e.message))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@EditProfileFragment.verificationId = verificationId
                binding.etOtp.visibility = View.VISIBLE
                binding.btnVerifyOtp.visibility = View.VISIBLE
                showToast("OTP sent")
            }
        }
    }

    private fun sendOtp(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(code: String) {
        val credential = verificationId?.let {
            PhoneAuthProvider.getCredential(it, code)
        } ?: return showError(getString(R.string.verification_id_missing))

        currentUser?.updatePhoneNumber(credential)
            ?.addOnSuccessListener {
                showToast(getString(R.string.phone_number_updated))
                originalPhone = binding.etPhone.text.toString().trim()
                binding.tvPhone.text = originalPhone
                cancelPhoneEdit()
            }
            ?.addOnFailureListener {
                showError(getString(R.string.failed_to_update_phone, it.message))
            }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
