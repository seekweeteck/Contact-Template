package my.edu.tarc.mycontact.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    //Create a LiveData with a Profile
    private val _profile : MutableLiveData<Profile> by lazy {
        MutableLiveData<Profile>()
    }

    init {
        _profile.value = Profile("","","")
    }

    //For encapsulation we use LiveData
    val profile: LiveData<Profile> get() = _profile

    fun setProfile(p: Profile){
        _profile.value = p
    }

}