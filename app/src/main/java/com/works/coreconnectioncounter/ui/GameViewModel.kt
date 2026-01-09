package com.works.coreconnectioncounter.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.works.coreconnectioncounter.CounterState
import com.works.coreconnectioncounter.PreferencesHelper

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

    // Preferences の保存/復元をラップして ViewModel 側で責任を持てるようにする
    fun saveState(context: Context, state: CounterState) {
        PreferencesHelper(context).save(state)
    }

    fun restoreState(context: Context): CounterState {
        return PreferencesHelper(context).restore()
    }

    // In-memory state
    var mode: String = "mode1"
    private val COUNTER_COUNT = 6
    private val SPINNER_AFFECTED_COUNT = 3

    private val _numbers = IntArray(COUNTER_COUNT)
    private val _bonuses = IntArray(SPINNER_AFFECTED_COUNT)

    var spinner1Index: Int = 0
    var spinner2Index: Int = 0
    var spinner1Name: String? = null
    var spinner2Name: String? = null

    var boost1Active: Boolean = false
    var boost2Active: Boolean = false

    fun initializeFromState(state: CounterState) {
        mode = state.mode
        spinner1Index = state.spinner1Index
        spinner2Index = state.spinner2Index
        spinner1Name = state.spinner1Name
        spinner2Name = state.spinner2Name
        for (i in _numbers.indices) _numbers[i] = state.numbers.getOrElse(i) { 0 }
        for (i in _bonuses.indices) _bonuses[i] = state.bonuses.getOrElse(i) { 0 }
        boost1Active = state.boost1Active
        boost2Active = state.boost2Active
        setAwakened(boost1Active)
        setCritical(boost2Active)
    }

    fun toCounterState(): CounterState {
        return CounterState(
            mode = mode,
            spinner1Index = spinner1Index,
            spinner2Index = spinner2Index,
            spinner1Name = spinner1Name,
            spinner2Name = spinner2Name,
            numbers = _numbers.copyOf(),
            bonuses = _bonuses.copyOf(),
            boost1Active = boost1Active,
            boost2Active = boost2Active
        )
    }

    fun setNumber(index: Int, value: Int) {
        if (index in _numbers.indices) {
            _numbers[index] = value
        }
    }

    fun addToNumber(index: Int, delta: Int) {
        if (index in _numbers.indices) {
            _numbers[index] += delta
        }
    }

    fun getNumber(index: Int): Int = if (index in _numbers.indices) _numbers[index] else 0

    fun setBonus(index: Int, value: Int) {
        if (index in _bonuses.indices) _bonuses[index] = value
    }

    fun getBonus(index: Int): Int = if (index in _bonuses.indices) _bonuses[index] else 0

    fun setSpinnerIndices(s1: Int, s2: Int) {
        spinner1Index = s1
        spinner2Index = s2
    }

    fun setSpinnerNames(n1: String?, n2: String?) {
        spinner1Name = n1
        spinner2Name = n2
    }

    fun setBoost1(active: Boolean) {
        boost1Active = active
        setAwakened(active)
    }

    fun setBoost2(active: Boolean) {
        boost2Active = active
        setCritical(active)
    }
}
