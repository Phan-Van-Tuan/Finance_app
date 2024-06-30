package com.project.financialManagement.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val option = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val weekdays = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val month = MutableLiveData<String>()
    val startDate = MutableLiveData<String>()
    val endDate = MutableLiveData<String>()
}