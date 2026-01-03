package com.works.coreconnectioncounter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _isAwakened = MutableLiveData<Boolean>(false)
    val isAwakened: LiveData<Boolean> = _isAwakened

    private val _isCritical = MutableLiveData<Boolean>(false)
    val isCritical: LiveData<Boolean> = _isCritical

    fun setAwakened(awakened: Boolean) {
        _isAwakened.value = awakened
    }

    fun setCritical(critical: Boolean) {
        _isCritical.value = critical
    }
}
