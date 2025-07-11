package com.attijariwafabank.devisesapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.adapter.AgencyAdapter
import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.databinding.FragmentAgenceBinding
import com.attijariwafabank.devisesapp.viewModels.MapsViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class AgencyFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentAgenceBinding
    private lateinit var viewModel: MapsViewModel
    private lateinit var agencyAdapter: AgencyAdapter
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var userLatLng: LatLng? = null
    private var isFirstLocationUpdate = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) enableUserLocation()
        else Toast.makeText(requireContext(),
            getString(R.string.permission_refuee), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAgenceBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MapsViewModel::class.java]
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        agencyAdapter = AgencyAdapter(emptyList(), requireContext())

        binding.agenciesRecyclerView.adapter = agencyAdapter
        binding.agenciesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.nearbyAgencies.observe(viewLifecycleOwner) { nearbyAgencies ->
            if (userLatLng != null) {
                updateMapAndList(nearbyAgencies, userLatLng!!)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        enableUserLocation()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        ).apply {
            setMinUpdateIntervalMillis(5000L)
            setMaxUpdateDelayMillis(15000L)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    userLatLng = LatLng(location.latitude, location.longitude)
                    viewModel.fetchNearbyAgencies(location.latitude.toString(), location.longitude.toString())
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng!!, 15f))

                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.position_introuvable), Toast.LENGTH_SHORT).show()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, requireActivity().mainLooper)
    }

    private fun updateMapAndList(agencies: List<Agency>, userLatLng: LatLng) {
        mMap.clear()

        val updatedAgencies = agencies.map {
            val rawDistance = it.distance.replace("m", "").toDoubleOrNull()
            val roundedDistance = if (rawDistance != null) String.format("%.2f m", rawDistance) else "N/A"
            it.copy(distance = roundedDistance)
        }.sortedBy {
            it.distance.replace("m", "").toDoubleOrNull() ?: Double.MAX_VALUE
        }

        agencyAdapter.updateAgencies(updatedAgencies)

        updatedAgencies.forEach {
            val lat = it.latitude.toDoubleOrNull()
            val lng = it.longitude.toDoubleOrNull()
            if (lat != null && lng != null) {
                mMap.addMarker(MarkerOptions().position(LatLng(lat, lng)).title(it.nom))
            }
        }

        val closest = updatedAgencies.firstOrNull()
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
