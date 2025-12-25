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
    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner

    private lateinit var numberTexts: Array<TextView>
    private lateinit var bonusTexts: Array<TextView>  // 加算値表示用
    private lateinit var plusButtons: Array<MaterialButton>
    private lateinit var minusButtons: Array<MaterialButton>
    private val currentNumbers = IntArray(3) { 0 }

    // 現在適用中の加算値を保持
    private val currentBonus = IntArray(3) { 0 }

    // Spinner1の各オプションに対応する加算値 [数字1, 数字2, 数字3]
    private val spinner1Values = arrayOf(
        intArrayOf(5, 3, 1),     // オプション1選択時
        intArrayOf(10, 6, 2),    // オプション2選択時
        intArrayOf(15, 9, 3)     // オプション3選択時
    )

    // Spinner2の各オプションに対応する加算値 [数字1, 数字2, 数字3]
    private val spinner2Values = arrayOf(
        intArrayOf(1, 1, 1),     // 選択A選択時
        intArrayOf(2, 2, 2),     // 選択B選択時
        intArrayOf(3, 3, 3)      // 選択C選択時
    )

    // 前回選択していたインデックスを保持
    private var previousSpinner1Index = 0
    private var previousSpinner2Index = 0

    // 初回選択をスキップするフラグ
    private var isFirstSelection1 = true
    private var isFirstSelection2 = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eques)

        spinner1 = findViewById(R.id.spinner1)
        spinner2 = findViewById(R.id.spinner2)

        val includeIds = arrayOf(R.id.numberSet1, R.id.numberSet2, R.id.numberSet3)

        numberTexts = Array(3) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.numberText)
        }
        bonusTexts = Array(3) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.bonusText)
        }
        plusButtons = Array(3) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.plusButton)
        }
        minusButtons = Array(3) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.minusButton)
        }

        for (i in 0..2) {
            numberTexts[i].text = currentNumbers[i].toString()
            bonusTexts[i].text = "(+0)"
            plusButtons[i].setOnClickListener {
                currentNumbers[i]++
                numberTexts[i].text = currentNumbers[i].toString()
            }
            minusButtons[i].setOnClickListener {
                currentNumbers[i]--
                numberTexts[i].text = currentNumbers[i].toString()
            }
        }

        setupSpinners()
    }

    private fun setupSpinners() {
        // セレクトボックス1のデータ
        val spinner1Items = arrayOf("オプション1", "オプション2", "オプション3")
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner1Items)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter1

        // セレクトボックス2のデータ
        val spinner2Items = arrayOf("選択A", "選択B", "選択C")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner2Items)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        // 初期値を1つ目に設定
        spinner1.setSelection(0, false)
        spinner2.setSelection(0, false)

        // セレクトボックス1の変更リスナー
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isFirstSelection1) {
                    isFirstSelection1 = false
                    return
                }
                // 前回の値を引いて、新しい値を足す
                subtractValuesFromNumbers(spinner1Values[previousSpinner1Index])
                addValuesToNumbers(spinner1Values[position])
                previousSpinner1Index = position
                updateBonusDisplay()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // セレクトボックス2の変更リスナー
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isFirstSelection2) {
                    isFirstSelection2 = false
                    return
                }
                // 前回の値を引いて、新しい値を足す
                subtractValuesFromNumbers(spinner2Values[previousSpinner2Index])
                addValuesToNumbers(spinner2Values[position])
                previousSpinner2Index = position
                updateBonusDisplay()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // 3つの数字に値を加算する
    private fun addValuesToNumbers(values: IntArray) {
        for (i in 0..2) {
            currentNumbers[i] += values[i]
            currentBonus[i] += values[i]
            numberTexts[i].text = currentNumbers[i].toString()
        }
    }

    // 3つの数字から値を減算する
    private fun subtractValuesFromNumbers(values: IntArray) {
        for (i in 0..2) {
            currentNumbers[i] -= values[i]
            currentBonus[i] -= values[i]
        }
    }

    // 加算値の表示を更新する
    private fun updateBonusDisplay() {
        for (i in 0..2) {
            val bonus = currentBonus[i]
            bonusTexts[i].text = if (bonus >= 0) "(+$bonus)" else "($bonus)"
        }
    }
}
