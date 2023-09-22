package my.edu.tarc.mycontact.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

    @WorkerThread
    suspend fun uploadContactList(contact_id: String){
        val firebaseDatabase = Firebase.database
        val myRef = firebaseDatabase.getReference("profile")
        for(contact in allContacts.value?.iterator()!!){
            with(myRef.child(contact_id)
                .child("contact_list")){

                child(contact.phone).child("name")
                    .setValue(contact.name)

                child(contact.phone).child("email")
                    .setValue(contact.email)

                child(contact.phone).child("phone")
                    .setValue(contact.phone)
            }//end of with
        }//end of for loop
    }
}