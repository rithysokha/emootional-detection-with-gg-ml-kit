package com.emotion.detector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotion.detector.api.ApiManager
import com.emotion.detector.model.ApiState
import com.emotion.detector.model.State
import kotlinx.coroutines.launch
import kotlin.math.log

class HomeViewModel : ViewModel() {

    private val _greetingState = MutableLiveData<ApiState<String>>()
    val greetingState: LiveData<ApiState<String>> = _greetingState

    fun loadGreeting(){
        val apiService = ApiManager.getApiService()
        viewModelScope.launch {
            try{
                val greetingResponse = apiService.loadGreet()
                if(greetingResponse.isSuccess()) {
                    _greetingState.postValue(ApiState(State.success, greetingResponse.data))
                }
                else{
                    _greetingState.postValue(ApiState(State.error, null))
                }
            }catch (e: Exception) {
                _greetingState.postValue(ApiState(State.error, e.message))
            }
        }
    }
}