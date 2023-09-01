package my.edu.tarc.mycontact.ui.contact_list

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class Contact(var name: String,
              var email: String,
              @PrimaryKey var phone: String) {
}