package my.edu.tarc.mycontact.ui.contact_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.edu.tarc.mycontact.ContactAdapter
import my.edu.tarc.mycontact.databinding.FragmentContactListBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val contactViewModel: ContactViewModel
            by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Define an instance of adapter
        val adapter = ContactAdapter()
        //Set datasource to the adapter
        contactViewModel.contactList.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                Toast.makeText(
                    context,
                    "No record",
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                    context,
                    "Record count : ${it.size}",
                    Toast.LENGTH_SHORT).show()
            }
            adapter.setContact(it)
        }
        //Attach adapter to the RecycleView
        binding.recyclerViewContact.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}