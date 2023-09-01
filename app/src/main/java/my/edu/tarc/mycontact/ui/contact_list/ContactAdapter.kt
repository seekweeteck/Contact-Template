package my.edu.tarc.mycontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.mycontact.ui.contact_list.Contact

class ContactAdapter() :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

    //private val dataSet: List<Contact>
    private var dataSet = emptyList<Contact>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
        val textViewPhone: TextView = view.findViewById(R.id.textViewPhone)

        init {
            // Define click listener for the ViewHolder's View.
            view.setOnClickListener {
            }
        }
    }

    internal fun setContact(contact: List<Contact>){
        dataSet = contact
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.record, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = dataSet[position]
        holder.textViewName.text = contact.name
        holder.textViewEmail.text = contact.email
        holder.textViewPhone.text = contact.phone
    }
}

