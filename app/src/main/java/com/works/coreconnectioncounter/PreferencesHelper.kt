package com.works.coreconnectioncounter

import android.content.Context
import android.content.SharedPreferences

data class CounterState(
    val mode: String = "mode1",
    val spinner1Index: Int = 0,
    val spinner2Index: Int = 0,
    val spinner1Name: String? = null,
    val spinner2Name: String? = null,
    val numbers: IntArray = IntArray(6),
    val bonuses: IntArray = IntArray(3),
    val boost1Active: Boolean = false,
    val boost2Active: Boolean = false
)

class PreferencesHelper(context: Context, prefsName: String = "eques_state") {
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    fun save(state: CounterState) {
        prefs.edit().apply {
            putString("MODE", state.mode)
            putInt("SPINNER1_INDEX", state.spinner1Index)
            putInt("SPINNER2_INDEX", state.spinner2Index)
            putString("SPINNER1_NAME", state.spinner1Name)
            putString("SPINNER2_NAME", state.spinner2Name)

            for (i in state.numbers.indices) {
                putInt("NUMBER_$i", state.numbers[i])
            }
            for (i in state.bonuses.indices) {
                putInt("BONUS_$i", state.bonuses[i])
            }

            putBoolean("BOOST1_ACTIVE", state.boost1Active)
            putBoolean("BOOST2_ACTIVE", state.boost2Active)
            apply()
        }
    }

    fun restore(): CounterState {
        val mode = prefs.getString("MODE", "mode1") ?: "mode1"
        val spinner1Index = prefs.getInt("SPINNER1_INDEX", 0)
        val spinner2Index = prefs.getInt("SPINNER2_INDEX", 0)
        val spinner1Name = prefs.getString("SPINNER1_NAME", null)
        val spinner2Name = prefs.getString("SPINNER2_NAME", null)

        val numbers = IntArray(6)
        for (i in numbers.indices) {
            numbers[i] = prefs.getInt("NUMBER_$i", 0)
        }

        val bonuses = IntArray(3)
        for (i in bonuses.indices) {
            bonuses[i] = prefs.getInt("BONUS_$i", 0)
        }

        val boost1 = prefs.getBoolean("BOOST1_ACTIVE", false)
        val boost2 = prefs.getBoolean("BOOST2_ACTIVE", false)

        return CounterState(
            mode = mode,
            spinner1Index = spinner1Index,
            spinner2Index = spinner2Index,
            spinner1Name = spinner1Name,
            spinner2Name = spinner2Name,
            numbers = numbers,
            bonuses = bonuses,
            boost1Active = boost1,
            boost2Active = boost2
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
