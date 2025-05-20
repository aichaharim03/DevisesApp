package com.attijariwafabank.devisesapp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.adapter.AgencyAdapter
import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.data.AgencyResponse
import com.attijariwafabank.devisesapp.databinding.FragmentAgenceBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson

class AgencyFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var agencies: List<Agency>
    private lateinit var binding: FragmentAgenceBinding
    private lateinit var agencyAdapter: AgencyAdapter // use your adapter


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        agencies = loadAgenciesFromJson()

        // RecyclerView setup
        binding.agenciesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        agencyAdapter = AgencyAdapter(agencies, requireContext()) // change to your adapter
        binding.agenciesRecyclerView.adapter = agencyAdapter
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        agencies.forEach { agency ->
            val lat = agency.latitude.toDouble()
            val lon = agency.longitude.toDouble()
            val marker = LatLng(lat, lon)
            mMap.addMarker(MarkerOptions().position(marker).title(agency.nom))
        }

        val closest = agencies.minByOrNull { it.distance.toDouble() }
        closest?.let {
            val closestLatLng = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(closestLatLng, 12f))
        }

        mMap.setOnMapClickListener { latLng ->
            val uri = Uri.parse("geo:${latLng.latitude},${latLng.longitude}?q=${latLng.latitude},${latLng.longitude}(Agency)")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    private fun loadAgenciesFromJson(): List<Agency> {
        val jsonString = context?.assets?.open("agencies.json")?.bufferedReader().use { it?.readText() }
        val gson = Gson()
        val agencyResponse = gson.fromJson(jsonString, AgencyResponse::class.java)
        return agencyResponse.agency
    }
}
