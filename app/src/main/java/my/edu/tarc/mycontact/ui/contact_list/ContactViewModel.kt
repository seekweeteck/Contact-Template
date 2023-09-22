package my.edu.tarc.mycontact.ui.contact_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.mycontact.database.ContactDao
import my.edu.tarc.mycontact.database.ContactDatabase
import my.edu.tarc.mycontact.database.ContactRepository

class ContactViewModel(application: Application): AndroidViewModel(application) {
    //val contactList = MutableLiveData<ArrayList<Contact>>()
    var contactList: LiveData<List<Contact>>
    private val repository: ContactRepository

    init {
        //val list = ArrayList<Contact>()
        //contactList.value = list

        //Create an instance of DB
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        //Connect DAO to repository
        repository = ContactRepository(contactDao)
        //Retrieve contact records
        contactList = repository.allContacts
    }

    //Coroutine
    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
    fun update(contact: Contact) = viewModelScope.launch {
        repository.update(contact)
    }

    fun uploadContactList(contact_id: String) = viewModelScope.launch {
        repository.uploadContactList(contact_id)
    }
}