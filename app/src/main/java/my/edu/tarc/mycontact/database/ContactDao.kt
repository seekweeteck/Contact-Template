package my.edu.tarc.mycontact.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.mycontact.ui.contact_list.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact ORDER BY name ASC")
    fun getAllContact(): LiveData<List<Contact>>

    @Query("DELETE FROM contact")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun update(contact: Contact)
}
