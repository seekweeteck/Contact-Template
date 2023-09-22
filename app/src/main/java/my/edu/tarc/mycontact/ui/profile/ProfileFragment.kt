package my.edu.tarc.mycontact.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.mycontact.R
import my.edu.tarc.mycontact.databinding.FragmentAddContactBinding
import my.edu.tarc.mycontact.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

class ProfileFragment : Fragment(), MenuProvider {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var myPreference: SharedPreferences

    val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()){
        //it = path to the profile picture
        binding.imageViewProfile.setImageURI(it)
    }

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

        readProfilePicture()

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
            saveProfile()
            saveProfilePicture()
            uploadProfilePicture()
        }//End of buttonSave

        binding.imageViewProfile.setOnClickListener {
            getContent.launch("image/*")
        }

    }//end of onViewCreated

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.findItem(R.id.action_settings).setVisible(false)
        menu.findItem(R.id.action_profile).setVisible(false)
        menu.findItem(R.id.action_about_us).setVisible(false)
        menu.findItem(R.id.action_upload).setVisible(false)
        menu.findItem(R.id.action_download).setVisible(false)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }

        return true
    }

    private fun saveProfile(){
        //Create an instance of Firebase Database
        val firebaseDatabase = Firebase.database
        //Create a reference to the database
        val myRef = firebaseDatabase.getReference("profile")
        with(myRef.child(profileViewModel.profile.value!!.phone)){
            child("name").setValue(
                profileViewModel.profile.value!!.name)

            child("email").setValue(
                profileViewModel.profile.value!!.email)

            child("phone").setValue(
                profileViewModel.profile.value!!.phone)
        }

    }

    private fun saveProfilePicture() {
        val filename = "profile.png"
        //this.context?.filesDir = get the app file directory
        val file = File(this.context?.filesDir, filename)

        val bd = binding.imageViewProfile.getDrawable() as BitmapDrawable
        val bitmap = bd.bitmap
        val outputStream: OutputStream

        try{
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }

    private fun uploadProfilePicture(){
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)
        //Create an instance of Firebase Storage
        val firebaseStorage = Firebase.storage
        //Create a reference (a folder) to the storage
        val myRef = firebaseStorage.reference
            .child("profile")
            .child(profileViewModel.profile.value!!.phone)

        //Upload the file to firebase storage
        myRef.putFile(file.toUri())
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "File Uploaded",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { }
    }

    private fun readProfilePicture(){
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)

        try{
            if(!file.exists()){
                binding.imageViewProfile.setImageResource(R.drawable.profile)
            }else{
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.imageViewProfile.setImageBitmap(bitmap)
            }
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }


}//End of Class