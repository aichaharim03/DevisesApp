package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.core.view.MenuProvider
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentMainPageBinding
import com.google.firebase.auth.FirebaseAuth

class MainPageFragment : Fragment() {

    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolBar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_tool_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.tool_bar_settings -> {
                        findNavController().navigate(R.id.action_mainPage_to_settings)  // Navigate to settings
                        true
                    }
                    R.id.tool_bar_profil -> {
                        findNavController().navigate(R.id.action_mainPage_to_profile)  // Navigate to profile
                        true
                    }
                    R.id.tool_bar_logout -> {
                        auth.signOut()
                        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
