package com.works.coreconnectioncounter

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

class SpinnerManager(
    private val context: Context,
    private val getMode: () -> String,
    private val isFirstSelection1: () -> Boolean,
    private val setFirstSelection1: (Boolean) -> Unit,
    private val isFirstSelection2: () -> Boolean,
    private val setFirstSelection2: (Boolean) -> Unit,
    private val onFirstSelection1: (Int) -> Unit,
    private val onFirstSelection2: (Int) -> Unit,
    private val onSelection1Changed: (Int) -> Unit,
    private val onSelection2Changed: (Int) -> Unit
) {

    fun attach(
        spinner1: Spinner,
        spinner2: Spinner,
        title1: TextView,
        title2: TextView,
        previousSpinner1Index: Int,
        previousSpinner2Index: Int,
        callSelectionCallback: Boolean = true
    ) {
        val mode = getMode()
        val spinner1Items = SpinnerData.getSpinner1Items(mode)
        val spinner2Items = SpinnerData.getSpinner2Items(mode)

        title1.text = SpinnerData.PILOT_TITLE
        title2.text = SpinnerData.MECHA_TITLE

        val adapter1 = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinner1Items)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter1

        val adapter2 = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinner2Items)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (isFirstSelection1()) {
                    setFirstSelection1(false)
                    onFirstSelection1(position)
                    return
                }
                onSelection1Changed(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (isFirstSelection2()) {
                    setFirstSelection2(false)
                    onFirstSelection2(position)
                    return
                }
                onSelection2Changed(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 復元されたインデックスで選択
        spinner1.setSelection(previousSpinner1Index, callSelectionCallback)
        spinner2.setSelection(previousSpinner2Index, callSelectionCallback)
    }
}
