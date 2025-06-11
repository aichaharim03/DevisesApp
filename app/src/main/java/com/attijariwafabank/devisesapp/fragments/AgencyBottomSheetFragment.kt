package com.attijariwafabank.devisesapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.databinding.FragmentAgencyBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AgencyBottomSheetFragment(private val agency: Agency) : BottomSheetDialogFragment() {

    private var binding : FragmentAgencyBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding= FragmentAgencyBottomSheetBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvAgencyName?.text = agency.nom
        binding?.tvAgencyAddress?.text = agency.adresse
        binding?.tvAgencyCity?.text = agency.ville
        binding?.tvDistance?.text = agency.distance
        binding?.tvPhone1?.text = agency.telephone1

        if (agency.telephone2.isNotBlank() && agency.telephone2 != agency.telephone1) {
            binding?.tvPhone2?.text = agency.telephone2
            binding?.tvPhone2?.visibility = View.VISIBLE
        } else {
            binding?.tvPhone2?.visibility = View.GONE
        }


        val workHours = listOf(
            agency.horaire1, agency.horaire2, agency.horaire3,
            agency.horaire4, agency.horaire5, agency.horaire6
        ).filter { it.isNotBlank() }

        binding?.tvHours?.text = workHours.joinToString("\n")


        binding?.btnRoute?.setOnClickListener{
            try {
                val uri = Uri.parse("google.navigation:q=${agency.latitude},${agency.longitude}")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.google.android.apps.maps")
                }

                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                } else {
                    val webUri = Uri.parse("https://maps.google.com/maps?daddr=${agency.latitude},${agency.longitude}")
                    val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                    startActivity(webIntent)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to open directions", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnCall?.setOnClickListener {
            val phoneNumber = when {
                agency.telephone1.isNotBlank() -> agency.telephone1
                agency.telephone2.isNotBlank() -> agency.telephone2
                else -> {
                    Toast.makeText(requireContext(), "No phone number available", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener

                }
            }

            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to make phone call", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnShare?.setOnClickListener {
            val mapUrl = "https://www.google.com/maps/search/?api=1&query=${agency.latitude},${agency.longitude}"

            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, mapUrl)
                    putExtra(Intent.EXTRA_SUBJECT, "Agency Location - ${agency.nom}")
                }
                startActivity(Intent.createChooser(intent, "Share agency location"))
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to share location", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
