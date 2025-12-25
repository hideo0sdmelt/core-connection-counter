package com.works.coreconnectioncounter

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class EquesActivity : AppCompatActivity() {
    companion object {
        private const val COUNTER_COUNT = 6
        private const val SPINNER_AFFECTED_COUNT = 3  // Spinnerが影響するのは上段3つ
    }

    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner

    private lateinit var numberTexts: Array<TextView>
    private lateinit var bonusTexts: Array<TextView>
    private lateinit var plusButtons: Array<MaterialButton>
    private lateinit var minusButtons: Array<MaterialButton>
    private val currentNumbers = IntArray(COUNTER_COUNT) { 0 }
    private val currentBonus = IntArray(SPINNER_AFFECTED_COUNT) { 0 }

    // Spinner1の各オプションに対応する加算値（上段3つ分）
    private val spinner1Values = arrayOf(
        intArrayOf(5, 3, 1),
        intArrayOf(10, 6, 2),
        intArrayOf(15, 9, 3)
    )

    // Spinner2の各オプションに対応する加算値（上段3つ分）
    private val spinner2Values = arrayOf(
        intArrayOf(1, 1, 1),
        intArrayOf(2, 2, 2),
        intArrayOf(3, 3, 3)
    )

    private var previousSpinner1Index = 0
    private var previousSpinner2Index = 0
    private var isFirstSelection1 = true
    private var isFirstSelection2 = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eques)

        spinner1 = findViewById(R.id.spinner1)
        spinner2 = findViewById(R.id.spinner2)

        // ✅ 6つ全部定義
        val includeIds = arrayOf(
            R.id.numberSet1, R.id.numberSet2, R.id.numberSet3,
            R.id.numberSet4, R.id.numberSet5, R.id.numberSet6
        )

        numberTexts = Array(COUNTER_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.numberText)
        }
        bonusTexts = Array(SPINNER_AFFECTED_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.bonusText)
        }
        plusButtons = Array(COUNTER_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.plusButton)
        }
        minusButtons = Array(COUNTER_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.minusButton)
        }

        // 全6つのカウンター初期化
        for (i in 0 until COUNTER_COUNT) {
            numberTexts[i].text = currentNumbers[i].toString()
            plusButtons[i].setOnClickListener {
                currentNumbers[i]++
                numberTexts[i].text = currentNumbers[i].toString()
            }
            minusButtons[i].setOnClickListener {
                currentNumbers[i]--
                numberTexts[i].text = currentNumbers[i].toString()
            }
        }

        // 上段3つのボーナス表示初期化
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            bonusTexts[i].text = "(+0)"
        }

        setupSpinners()
    }

    private fun setupSpinners() {
        val spinner1Items = arrayOf("オプション1", "オプション2", "オプション3")
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner1Items)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter1

        val spinner2Items = arrayOf("選択A", "選択B", "選択C")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner2Items)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        spinner1.setSelection(0, false)
        spinner2.setSelection(0, false)

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isFirstSelection1) {
                    isFirstSelection1 = false
                    return
                }
                subtractValuesFromNumbers(spinner1Values[previousSpinner1Index])
                addValuesToNumbers(spinner1Values[position])
                previousSpinner1Index = position
                updateBonusDisplay()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isFirstSelection2) {
                    isFirstSelection2 = false
                    return
                }
                subtractValuesFromNumbers(spinner2Values[previousSpinner2Index])
                addValuesToNumbers(spinner2Values[position])
                previousSpinner2Index = position
                updateBonusDisplay()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // 上段3つの数字に値を加算する
    private fun addValuesToNumbers(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbers[i] += values[i]
            currentBonus[i] += values[i]
            numberTexts[i].text = currentNumbers[i].toString()
        }
    }

    // 上段3つの数字から値を減算する
    private fun subtractValuesFromNumbers(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbers[i] -= values[i]
            currentBonus[i] -= values[i]
        }
    }

    // 上段3つの加算値表示を更新する
    private fun updateBonusDisplay() {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val bonus = currentBonus[i]
            bonusTexts[i].text = if (bonus >= 0) "(+$bonus)" else "($bonus)"
        }
    }
}
