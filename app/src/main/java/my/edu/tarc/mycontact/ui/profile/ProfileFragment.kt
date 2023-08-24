package my.edu.tarc.mycontact.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import my.edu.tarc.mycontact.R
import my.edu.tarc.mycontact.databinding.FragmentAddContactBinding
import my.edu.tarc.mycontact.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), MenuProvider {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var myPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        //Add support for menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPreference = requireActivity().getPreferences(Context.MODE_PRIVATE)

        with(profileViewModel.profile.value) {
            this!!.name = myPreference.getString(getString(R.string.name), "").toString()
            email = myPreference.getString(getString(R.string.email), "").toString()
            phone = myPreference.getString(getString(R.string.phone), "").toString()
        }

        profileViewModel.profile.observe(viewLifecycleOwner) {
            binding.editTextProfileName.setText(it.name)
            binding.editTextProfileEmail.setText(it.email)
            binding.editTextProfilePhone.setText(it.phone)
        }

        binding.buttonSaveProfile.setOnClickListener {
            profileViewModel.setProfile(
                Profile(
                    binding.editTextProfileName.text.toString(),
                    binding.editTextProfileEmail.text.toString(),
                    binding.editTextProfilePhone.text.toString()
                )
            )

            with(myPreference.edit()) {
                putString(
                    getString(R.string.name),
                    binding.editTextProfileName.text.toString()
                )
                putString(
                    getString(R.string.email),
                    binding.editTextProfileEmail.text.toString()
                )
                putString(
                    getString(R.string.phone),
                    binding.editTextProfilePhone.text.toString()
                )
                apply()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.findItem(R.id.action_settings).setVisible(false)
        menu.findItem(R.id.action_profile).setVisible(false)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }

        return true
    }

}