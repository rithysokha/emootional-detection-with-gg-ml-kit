package com.emotion.detector.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotion.detector.api.ApiManager
import com.emotion.detector.model.ApiState
import com.emotion.detector.model.Profile
import com.emotion.detector.model.State
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _profileState = MutableLiveData<ApiState<Profile>>()
    val profileState: LiveData<ApiState<Profile>> = _profileState

    fun loadProfile(){
        val apiService = ApiManager.getApiService()

        viewModelScope.launch {
            try {
                val profileResponse = apiService.loadProfile(2)
                if(profileResponse.isSuccess()){
                    Log.d("ProfileViewModel", "loadProfile: ${profileResponse.data}")
                    _profileState.postValue(ApiState(State.success, profileResponse.data))
                }else{
                    _profileState.postValue(ApiState(State.error, null))
                }
            }catch (e: Exception){
                _profileState.postValue(ApiState(State.error, null))
            }
        }
    }
}