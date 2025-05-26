package com.attijariwafabank.devisesapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.adapter.AgencyAdapter
import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.data.AgencyResponse
import com.attijariwafabank.devisesapp.databinding.FragmentAgenceBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlin.math.*

class AgencyFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentAgenceBinding
    private lateinit var agencyAdapter: AgencyAdapter
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var agencies: List<Agency> = emptyList()
    private var isFirstLocationUpdate = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) enableUserLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgenceBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        agencies = loadAgenciesFromJson()

        binding.agenciesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        agencyAdapter = AgencyAdapter(agencies, requireContext())
        binding.agenciesRecyclerView.adapter = agencyAdapter
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        enableUserLocation()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                updateMapAndList(it)
            }
        }

        // Optionally, you can request location updates if you want continuous tracking:
        /*
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                updateMapAndList(location)
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        */
    }

    private fun updateMapAndList(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)

        // Calculer distances et trier agences
        agencies = agencies.map {
            val agencyLat = it.latitude.toDoubleOrNull()
            val agencyLng = it.longitude.toDoubleOrNull()
            if (agencyLat != null && agencyLng != null) {
                val agencyLatLng = LatLng(agencyLat, agencyLng)
                val distanceKm = distanceBetween(userLatLng, agencyLatLng)
                it.copy(distance = String.format("%.2f km", distanceKm))
            } else {
                it.copy(distance = "N/A")
            }
        }.sortedBy {
            it.distance.replace(" km", "").toDoubleOrNull() ?: Double.MAX_VALUE
        }

        agencyAdapter.updateAgencies(agencies)

        // Mise à jour de la carte
        mMap.clear()
        agencies.forEach {
            val lat = it.latitude.toDoubleOrNull()
            val lng = it.longitude.toDoubleOrNull()
            if (lat != null && lng != null) {
                val latLng = LatLng(lat, lng)
                mMap.addMarker(MarkerOptions().position(latLng).title(it.nom))
            }
        }

        val closest = agencies.firstOrNull()
        val closestLat = closest?.latitude?.toDoubleOrNull()
        val closestLng = closest?.longitude?.toDoubleOrNull()
        if (closestLat != null && closestLng != null) {
            val closestLatLng = LatLng(closestLat, closestLng)
            if (isFirstLocationUpdate) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
                isFirstLocationUpdate = false
            }
            mMap.addPolyline(
                PolylineOptions()
                    .add(userLatLng, closestLatLng)
                    .width(5f)
                    .color(ContextCompat.getColor(requireContext(), R.color.teal_700))
            )
        }
    }

    private fun distanceBetween(start: LatLng, end: LatLng): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)

        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun loadAgenciesFromJson(): List<Agency> {
        val jsonString = context?.assets?.open("agencies.json")?.bufferedReader().use { it?.readText() }
        val gson = Gson()
        val agencyResponse = gson.fromJson(jsonString, AgencyResponse::class.java)
        return agencyResponse.agency
    }
}
