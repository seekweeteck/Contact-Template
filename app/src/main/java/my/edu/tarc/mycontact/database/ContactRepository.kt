package my.edu.tarc.mycontact.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.mycontact.ui.contact_list.Contact

class ContactRepository(private val contactDao: ContactDao) {
    //Room execute all queries on a separate thread
    val allContacts: LiveData<List<Contact>> = contactDao.getAllContact()

    @WorkerThread
    suspend fun insert(contact: Contact){
        contactDao.insert(contact)
    }

    @WorkerThread
    suspend fun delete(contact: Contact){
        contactDao.delete(contact)
    }

    @WorkerThread
    suspend fun deleteAll(){
        contactDao.deleteAll()
    }

    @WorkerThread
    suspend fun update(contact: Contact){
        contactDao.update(contact)
    }
}