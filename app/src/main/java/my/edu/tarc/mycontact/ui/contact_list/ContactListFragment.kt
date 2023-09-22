package my.edu.tarc.mycontact.ui.contact_list

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import my.edu.tarc.mycontact.ContactAdapter
import my.edu.tarc.mycontact.R
import my.edu.tarc.mycontact.WebDB
import my.edu.tarc.mycontact.databinding.FragmentContactListBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.UnknownHostException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ContactListFragment : Fragment(), MenuProvider {

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

        //Add support for menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
       //DO NOTHING
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_download){
            //Download Contact records from a web server
            val myPreference: SharedPreferences =
                requireActivity().getPreferences(Context.MODE_PRIVATE)
            val contact_id = myPreference.getString(
                getString(R.string.phone), "")
            //Make a call to the downloadContact function
            downloadContact(requireContext(),
            getString(R.string.read_url) +
                    "contact_id=" + contact_id)
        }else if(menuItem.itemId == R.id.action_upload){
            val myPreference: SharedPreferences =
                requireActivity().getPreferences(Context.MODE_PRIVATE)
            val contact_id = myPreference.getString(
                getString(R.string.phone), "")
            if(!contact_id.isNullOrEmpty()){
                contactViewModel.uploadContactList(contact_id)
            }

        }
        return true
    }

    fun downloadContact(context: Context, url: String){
        binding.progressBar.visibility = View.VISIBLE
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Process the JSON
                try {
                    if (response != null) {
                        val strResponse = response.toString()
                        val jsonResponse = JSONObject(strResponse)
                        val jsonArray: JSONArray = jsonResponse.getJSONArray("records")
                        val size: Int = jsonArray.length()

                        if(contactViewModel.contactList.value?.isNotEmpty()!!){
                            contactViewModel.deleteAll()
                        }

                        for (i in 0..size - 1) {
                            var jsonContact: JSONObject = jsonArray.getJSONObject(i)
                            var contact = Contact(
                                jsonContact.getString("name"),
                                jsonContact.getString("email"),
                                jsonContact.getString("phone")
                            )
                            contactViewModel.insert(
                                Contact(contact?.name!!,
                                    contact?.email!!,
                                    contact?.phone!!)
                            )
                        }
                        Toast.makeText(context, "$size record(s) downloaded", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }catch (e: UnknownHostException){
                    Log.d("ContactRepository", "Unknown Host: %s".format(e.message.toString()))
                    binding.progressBar.visibility = View.INVISIBLE
                }
                catch (e: Exception) {
                    Log.d("ContactRepository", "Response: %s".format(e.message.toString()))
                    binding.progressBar.visibility = View.INVISIBLE
                }
            },
            { error ->
                Log.d("ContactRepository", "Error Response: %s".format(error.message.toString()))
            },
        )

        //Volley request policy, only one time request
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, //no retry
            1f
        )

        // Access the RequestQueue through your singleton class.
        WebDB.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }
}
