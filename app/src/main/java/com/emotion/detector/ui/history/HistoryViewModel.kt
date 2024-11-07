package com.emotion.detector.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotion.detector.api.ApiManager
import com.emotion.detector.model.ApiState
import com.emotion.detector.model.History
import com.emotion.detector.model.State
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val _historyState = MutableLiveData<ApiState<List<History>>>()
    val historyState: LiveData<ApiState<List<History>>> = _historyState

    fun loadHistory(){
        val apiService = ApiManager.getApiService()

        viewModelScope.launch{
            try{
                val historyResponse = apiService.loadHistory()
                if(historyResponse.isSuccess()){
                    Log.d("HistoryViewModel","History loaded successfully")
                    _historyState.postValue(ApiState(State.success,historyResponse.data))
                }else{
                    Log.d("HistoryViewModel","Failed to load history")
                    _historyState.postValue(ApiState(State.error,null))
                }
            }catch (e:Exception){
                Log.d("HistoryViewModel","Failed to load history")
                _historyState.postValue(ApiState(State.error,null))
            }
        }
    }
}