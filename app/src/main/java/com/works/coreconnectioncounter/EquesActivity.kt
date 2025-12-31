package com.works.coreconnectioncounter

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class EquesActivity : AppCompatActivity() {
    companion object {
        private const val COUNTER_COUNT = 6
        private const val SPINNER_AFFECTED_COUNT = 3
    }

    private lateinit var toolbar: MaterialToolbar

    private lateinit var homeButton: MaterialButton
    private lateinit var resetButton: MaterialButton

    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinnerTitle1: TextView
    private lateinit var spinnerTitle2: TextView

    private lateinit var multiplierButton1: MaterialButton
    private lateinit var multiplierButton2: MaterialButton

    private lateinit var numberTexts: Array<TextView>
    private lateinit var titleTexts: Array<TextView>
    private lateinit var plusButtons: Array<MaterialButton>
    private lateinit var minusButtons: Array<MaterialButton>

    private val currentNumbers = IntArray(COUNTER_COUNT) { 0 }
    private val currentBonus = IntArray(SPINNER_AFFECTED_COUNT) { 0 }

    private var previousSpinner1Index = 0
    private var previousSpinner2Index = 0
    private var isFirstSelection1 = true
    private var isFirstSelection2 = true

    // 🆕 ブースト状態（ON/OFF）
    private var isBoost1Active = false
    private var isBoost2Active = false

    // 🆕 モード情報
    private var currentMode = "mode1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eques)

        // ✅ バックボタンの処理を登録
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveState()
                finish()
            }
        })

        // Intentからモード情報を取得
        currentMode = intent.getStringExtra("MODE") ?: "mode1"

        setupHomeButton()
        setupResetButton()
        setupToolbar()

        setupSpinnerViews()
        setupCounterViews()
        setupMultiplierButtons()
        setupSpinners()

        updateAllTitles()

        // ✅ 保存されたデータを復元
        restoreState()
    }

    override fun onPause() {
        super.onPause()
        // ✅ 画面を離れる時に保存
        saveState()
    }


    override fun onSupportNavigateUp(): Boolean {
        saveState()
        finish()
        return true
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
    }

    private fun setupHomeButton() {
        homeButton = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {
            saveState()
            finish()
        }
    }

    // ✅ リセットボタンの処理
    private fun setupResetButton() {
        resetButton = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            showResetConfirmDialog()
        }
    }

    private fun showResetConfirmDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("リセット確認")
            .setMessage("すべての設定値を初期値に戻します。\nよろしいですか？")
            .setPositiveButton("OK") { _, _ ->
                resetAllValues()
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun resetAllValues() {
        // ✅ すべてのカウンターを0にリセット
        for (i in 0 until COUNTER_COUNT) {
            currentNumbers[i] = 0
            numberTexts[i].text = "0"
        }

        // ✅ ボーナスをリセット
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentBonus[i] = 0
        }

        // ✅ スピナーを最初の項目に
        spinner1.setSelection(0, false)
        spinner2.setSelection(0, false)

        // ✅ ブースト状態をリセット
        isBoost1Active = false
        isBoost2Active = false
        updateButtonAppearance()

        // ✅ SharedPreferences をクリア
        val prefs = getSharedPreferences("eques_state", MODE_PRIVATE)
        prefs.edit().clear().apply()

        // ✅ UI更新
        updateAllTitles()
    }

    // スピナーの設定
    private fun setupSpinnerViews() {
        val spinnerSet1 = findViewById<View>(R.id.spinnerSet1)
        val spinnerSet2 = findViewById<View>(R.id.spinnerSet2)

        spinner1 = spinnerSet1.findViewById(R.id.spinner)
        spinner2 = spinnerSet2.findViewById(R.id.spinner)
        spinnerTitle1 = spinnerSet1.findViewById(R.id.spinnerTitle)
        spinnerTitle2 = spinnerSet2.findViewById(R.id.spinnerTitle)

        spinnerTitle1.text = SpinnerData.SPINNER1_TITLE
        spinnerTitle2.text = SpinnerData.SPINNER2_TITLE
    }

    // カウンターの設定
    private fun setupCounterViews() {
        val includeIds = arrayOf(
            R.id.numberSet1, R.id.numberSet2, R.id.numberSet3,
            R.id.numberSet4, R.id.numberSet5, R.id.numberSet6
        )

        numberTexts = Array(COUNTER_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.numberText)
        }
        titleTexts = Array(COUNTER_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.titleText)
        }
        plusButtons = Array(COUNTER_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.plusButton)
        }
        minusButtons = Array(COUNTER_COUNT) { i ->
            findViewById<View>(includeIds[i]).findViewById(R.id.minusButton)
        }

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
    }

    // 
    private fun setupMultiplierButtons() {
        val multiplierButtonsView = findViewById<View>(R.id.multiplierButtons)
        multiplierButton1 = multiplierButtonsView.findViewById(R.id.multiplierButton1)
        multiplierButton2 = multiplierButtonsView.findViewById(R.id.multiplierButton2)

        multiplierButton1.text = SpinnerData.BOOST1_BUTTON_TITLE
        multiplierButton2.text = SpinnerData.BOOST2_BUTTON_TITLE

        multiplierButton1.setOnClickListener { toggleBoost1() }

        multiplierButton2.setOnClickListener { toggleBoost2() }

        updateButtonAppearance()
    }

    private fun toggleBoost1() {
        // 🆕 モードに応じたブースト値を取得
        val boostValues = SpinnerData.getSpinner1BoostValues(currentMode)[previousSpinner1Index]

        if (isBoost1Active) {
            subtractValuesFromNumbers(boostValues)
        } else {
            addValuesToNumbers(boostValues)
        }

        isBoost1Active = !isBoost1Active
        updateButtonAppearance()
        updateAllTitles()
    }

    private fun toggleBoost2() {
        // 🆕 モードに応じたブースト値を取得
        val boostValues = SpinnerData.getSpinner2BoostValues(currentMode)[previousSpinner2Index]

        if (isBoost2Active) {
            subtractValuesFromNumbers(boostValues)
        } else {
            addValuesToNumbers(boostValues)
        }

        isBoost2Active = !isBoost2Active
        updateButtonAppearance()
        updateAllTitles()
    }

    // ボタンの表示を更新
    private fun updateButtonAppearance() {
        multiplierButton1.alpha = if (isBoost1Active) 1.0f else 0.5f
        multiplierButton2.alpha = if (isBoost2Active) 1.0f else 0.5f
    }

    private fun setupSpinners() {
        // 🆕 モードに応じたアイテムを取得
        val spinner1Items = SpinnerData.getSpinner1Items(currentMode)
        val spinner2Items = SpinnerData.getSpinner2Items(currentMode)

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner1Items)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter1

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner2Items)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (isFirstSelection1) {
                    isFirstSelection1 = false
                    previousSpinner1Index = position
                    val spinner1Values = SpinnerData.getSpinner1Values(currentMode)
                    addValuesToNumbers(spinner1Values[position])
                    updateAllTitles()
                    return
                }
                onSpinner1Changed(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (isFirstSelection2) {
                    isFirstSelection2 = false
                    previousSpinner2Index = position
                    val spinner2Values = SpinnerData.getSpinner2Values(currentMode)
                    addValuesToNumbers(spinner2Values[position])
                    updateAllTitles()
                    return
                }
                onSpinner2Changed(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 🆕 setSelection(0, true) に変更 → リスナーが呼ばれる
        spinner1.setSelection(0, true)
        spinner2.setSelection(0, true)
    }

    private fun onSpinner1Changed(newPosition: Int) {
        // 🆕 モードに応じた値を取得
        val spinner1Values = SpinnerData.getSpinner1Values(currentMode)
        val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(currentMode)

        subtractValuesFromNumbers(spinner1Values[previousSpinner1Index])
        if (isBoost1Active) {
            subtractValuesFromNumbers(spinner1BoostValues[previousSpinner1Index])
        }

        addValuesToNumbers(spinner1Values[newPosition])
        if (isBoost1Active) {
            addValuesToNumbers(spinner1BoostValues[newPosition])
        }

        previousSpinner1Index = newPosition
        updateAllTitles()
    }

    private fun onSpinner2Changed(newPosition: Int) {
        // 🆕 モードに応じた値を取得
        val spinner2Values = SpinnerData.getSpinner2Values(currentMode)
        val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(currentMode)

        subtractValuesFromNumbers(spinner2Values[previousSpinner2Index])
        if (isBoost2Active) {
            subtractValuesFromNumbers(spinner2BoostValues[previousSpinner2Index])
        }

        addValuesToNumbers(spinner2Values[newPosition])
        if (isBoost2Active) {
            addValuesToNumbers(spinner2BoostValues[newPosition])
        }

        previousSpinner2Index = newPosition
        updateAllTitles()
    }

    private fun addValuesToNumbers(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbers[i] += values[i]
            currentBonus[i] += values[i]
            numberTexts[i].text = currentNumbers[i].toString()
        }
    }

    private fun subtractValuesFromNumbers(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbers[i] -= values[i]
            currentBonus[i] -= values[i]
            numberTexts[i].text = currentNumbers[i].toString()
        }
    }

    private fun updateAllTitles() {
        for (i in 0 until COUNTER_COUNT) {
            val title = SpinnerData.COUNTER_TITLES[i]
            if (i < SPINNER_AFFECTED_COUNT) {
                val bonus = currentBonus[i]
                val bonusStr = if (bonus >= 0) "(+$bonus)" else "($bonus)"
                "$title$bonusStr".also { titleTexts[i].text = it }
            } else {
                titleTexts[i].text = title
            }
        }
    }

    private fun saveState() {
        val prefs = getSharedPreferences("eques_state", MODE_PRIVATE)
        prefs.edit().apply {
            putString("MODE", currentMode)
            putInt("SPINNER1_INDEX", spinner1.selectedItemPosition)
            putInt("SPINNER2_INDEX", spinner2.selectedItemPosition)
            for (i in 0 until COUNTER_COUNT) {
                putInt("NUMBER_$i", currentNumbers[i])
            }
            for (i in 0 until SPINNER_AFFECTED_COUNT) {
                putInt("BONUS_$i", currentBonus[i])
            }
            putBoolean("BOOST1_ACTIVE", isBoost1Active)
            putBoolean("BOOST2_ACTIVE", isBoost2Active)
            apply()
        }
    }

    private fun restoreState() {
        val prefs = getSharedPreferences("eques_state", MODE_PRIVATE)
        currentMode = prefs.getString("MODE", "mode1") ?: "mode1"

        val spinner1Index = prefs.getInt("SPINNER1_INDEX", 0)
        val spinner2Index = prefs.getInt("SPINNER2_INDEX", 0)

        for (i in 0 until COUNTER_COUNT) {
            currentNumbers[i] = prefs.getInt("NUMBER_$i", 0)
        }
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentBonus[i] = prefs.getInt("BONUS_$i", 0)
        }

        isBoost1Active = prefs.getBoolean("BOOST1_ACTIVE", false)
        isBoost2Active = prefs.getBoolean("BOOST2_ACTIVE", false)

        // UI更新
        for (i in 0 until COUNTER_COUNT) {
            numberTexts[i].text = currentNumbers[i].toString()
        }

        spinner1.setSelection(spinner1Index, false)
        spinner2.setSelection(spinner2Index, false)

        updateButtonAppearance()
        updateAllTitles()
    }

}
