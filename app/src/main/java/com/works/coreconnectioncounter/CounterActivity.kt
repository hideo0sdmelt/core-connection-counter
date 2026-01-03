package com.works.coreconnectioncounter

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class CounterActivity : AppCompatActivity() {
    companion object {
        private const val COUNTER_COUNT = 6
        private const val SPINNER_AFFECTED_COUNT = 3
    }

    // ツールバーとボタン
    private lateinit var toolbar: MaterialToolbar
    private lateinit var homeButton: MaterialButton
    private lateinit var resetButton: MaterialButton

    // スピナーと関連ビュー
    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinnerTitle1: TextView
    private lateinit var spinnerTitle2: TextView

    // ブーストボタン
    private lateinit var multiplierButton1: MaterialButton
    private lateinit var multiplierButton2: MaterialButton

    // カウンター関連ビュー
    private lateinit var numberTexts: Array<TextView>
    private lateinit var titleTexts: Array<TextView>
    private lateinit var plusButtons: Array<MaterialButton>
    private lateinit var minusButtons: Array<MaterialButton>

    // カウンター値とボーナス値
    private val currentNumbers = IntArray(COUNTER_COUNT)
    private val currentBonus = IntArray(SPINNER_AFFECTED_COUNT)

    // スピナー選択状態
    private var previousSpinner1Index = 0
    private var previousSpinner2Index = 0
    private var isFirstSelection1 = true
    private var isFirstSelection2 = true

    // ブースト状態
    private var isBoost1Active = false
    private var isBoost2Active = false

    // モード情報
    private var currentMode = "mode1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        // バックボタンの処理を登録
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

        // 保存されたデータを復元
        restoreState()
    }

    override fun onPause() {
        super.onPause()
        // 画面を離れる時に保存
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
        // すべてのカウンターを0にリセット
        for (i in 0 until COUNTER_COUNT) {
            currentNumbers[i] = 0
            numberTexts[i].text = "0"
        }

        // ボーナスをリセット
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentBonus[i] = 0
        }

        // ブースト状態をリセット
        isBoost1Active = false
        isBoost2Active = false

        // SharedPreferencesをクリア
        val prefs = getSharedPreferences("eques_state", MODE_PRIVATE)
        prefs.edit { clear() }

        // スピナーをリセット（リスナーが呼ばれるように true に変更）
        isFirstSelection1 = true
        isFirstSelection2 = true
        spinner1.setSelection(0, true)
        spinner2.setSelection(0, true)

        updateButtonAppearance()
        updateAllTitles()
    }

    // スピナーのビューを初期化
    private fun setupSpinnerViews() {
        val spinnerSet1 = findViewById<View>(R.id.spinnerSet1)
        val spinnerSet2 = findViewById<View>(R.id.spinnerSet2)

        spinner1 = spinnerSet1.findViewById(R.id.spinner)
        spinner2 = spinnerSet2.findViewById(R.id.spinner)
        spinnerTitle1 = spinnerSet1.findViewById(R.id.spinnerTitle)
        spinnerTitle2 = spinnerSet2.findViewById(R.id.spinnerTitle)

        spinnerTitle1.text = SpinnerData.PILOT_TITLE
        spinnerTitle2.text = SpinnerData.MECHA_TITLE
    }

    // カウンターのビューを初期化
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

        // 各カウンターにクリックリスナーを設定
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

    // ブーストボタンを初期化
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

    // クラスメンバーに追加
    private val baseNumbers = IntArray(COUNTER_COUNT)

    // 覚醒ボタン（パイロット依存）のトグル処理
    private fun toggleBoost1() {
        // パイロットが初期値の場合は処理しない
        if (spinner1.selectedItemPosition == 0) return

        // ===== パイロット関連の値を取得 =====
        val pilotBaseValues = SpinnerData.getSpinner1Values(currentMode)[previousSpinner1Index]
        val pilotBoostValues = SpinnerData.getSpinner1BoostValues(currentMode)[previousSpinner1Index]

        // ===== 機体のベース値を取得（ブースト時も変わらない） =====
        val machineBaseValues = SpinnerData.getSpinner2Values(currentMode)[previousSpinner2Index]

        if (isBoost1Active) {
            // ===== ブースト状態をOFFにする =====
            // パイロットブースト分を削除して元の値に戻す
            for (i in 0 until SPINNER_AFFECTED_COUNT) {
                // 現在：（パイロットブースト + 機体ベース + 手動）
                // 目標：（パイロット元 + 機体ベース + 手動）
                currentNumbers[i] = pilotBaseValues[i] + machineBaseValues[i] + currentBonus[i]
                numberTexts[i].text = currentNumbers[i].toString()
            }
        } else {
            // ===== ブースト状態をONにする =====
            // パイロットをブースト値に変更
            for (i in 0 until SPINNER_AFFECTED_COUNT) {
                // 現在：（パイロット元 + 機体ベース + 手動）
                // 目標：（パイロットブースト + 機体ベース + 手動）
                currentNumbers[i] = pilotBoostValues[i] + machineBaseValues[i] + currentBonus[i]
                numberTexts[i].text = currentNumbers[i].toString()
            }
        }

        isBoost1Active = !isBoost1Active
        updateButtonAppearance()
        updateAllTitles()
    }

    // 臨界ボタン（機体依存）のトグル処理
    private fun toggleBoost2() {
        // 機体が初期値の場合は処理しない
        if (spinner2.selectedItemPosition == 0) return

        val boostValues = SpinnerData.getSpinner2BoostValues(currentMode)[previousSpinner2Index]
        val baseValues = SpinnerData.getSpinner2Values(currentMode)[previousSpinner2Index]

        if (isBoost2Active) {
            // ブースト状態をOFFにする → ベース値に戻す
            for (i in 0 until SPINNER_AFFECTED_COUNT) {
                currentNumbers[i] -= boostValues[i]
                currentBonus[i] = 0
                numberTexts[i].text = currentNumbers[i].toString()
            }
        } else {
            // ブースト状態をONにする → ブースト値を適用
            for (i in 0 until SPINNER_AFFECTED_COUNT) {
                currentNumbers[i] += boostValues[i]
                currentBonus[i] = boostValues[i]
                numberTexts[i].text = currentNumbers[i].toString()
            }
        }

        isBoost2Active = !isBoost2Active
        updateButtonAppearance()
        updateAllTitles()
    }


    // ボタンの有効/無効と透明度を更新
    private fun updateButtonAppearance() {
        // パイロット初期値（index=0）の場合は覚醒ボタンを無効化
        val isSpinner1Default = spinner1.selectedItemPosition == 0
        // 機体初期値（index=0）の場合は臨界ボタンを無効化
        val isSpinner2Default = spinner2.selectedItemPosition == 0

        // 覚醒ボタン（パイロット依存）
        multiplierButton1.isEnabled = !isSpinner1Default
        multiplierButton1.alpha = if (isBoost1Active && !isSpinner1Default) 1.0f else 0.5f

        // 臨界ボタン（機体依存）
        multiplierButton2.isEnabled = !isSpinner2Default
        multiplierButton2.alpha = if (isBoost2Active && !isSpinner2Default) 1.0f else 0.5f
    }

    // スピナーを初期化してアダプターを設定
    private fun setupSpinners() {
        // モードに応じたアイテムを取得
        val spinner1Items = SpinnerData.getSpinner1Items(currentMode)
        val spinner2Items = SpinnerData.getSpinner2Items(currentMode)

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner1Items)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter1

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner2Items)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        // パイロットスピナーのリスナー
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                // 初回選択時の処理
                if (isFirstSelection1) {
                    isFirstSelection1 = false
                    previousSpinner1Index = position
                    val spinner1Values = SpinnerData.getSpinner1Values(currentMode)
                    addValuesToNumbers(spinner1Values[position])

                    // ★ 初期値（選抜パイロット）のボーナス値を設定
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonus[i] = spinner1Values[position][i]
                    }

                    updateAllTitles()
                    updateButtonAppearance()
                    return
                }
                // 2回目以降の選択時の処理
                onSpinner1Changed(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 機体スピナーのリスナー
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                // 初回選択時の処理
                if (isFirstSelection2) {
                    isFirstSelection2 = false
                    previousSpinner2Index = position
                    val spinner2Values = SpinnerData.getSpinner2Values(currentMode)
                    addValuesToNumbers(spinner2Values[position])

                    // ★ 初期値（量産機）のボーナス値を設定
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonus[i] += spinner2Values[position][i]
                    }

                    updateAllTitles()
                    updateButtonAppearance()
                    return
                }
                // 2回目以降の選択時の処理
                onSpinner2Changed(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 初期選択を設定（リスナーが呼ばれる）
        spinner1.setSelection(0, true)
        spinner2.setSelection(0, true)
    }

    // パイロット選択変更時の処理
    private fun onSpinner1Changed(newPosition: Int) {
        val spinner1Values = SpinnerData.getSpinner1Values(currentMode)
        val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(currentMode)
        val spinner2Values = SpinnerData.getSpinner2Values(currentMode)
        val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(currentMode)

        // 前の選択値を減算
        subtractValuesFromNumbers(spinner1Values[previousSpinner1Index])
        if (isBoost1Active) {
            subtractValuesFromNumbers(spinner1BoostValues[previousSpinner1Index])
        }

        // 新しい選択値を加算
        addValuesToNumbers(spinner1Values[newPosition])
        if (isBoost1Active) {
            addValuesToNumbers(spinner1BoostValues[newPosition])
        }

        // ★ currentBonus を更新（パイロット + 機体）
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val pilotBonus = if (isBoost1Active) {
                spinner1BoostValues[newPosition][i]
            } else {
                spinner1Values[newPosition][i]
            }

            val machineBonus = if (isBoost2Active) {
                spinner2BoostValues[previousSpinner2Index][i]
            } else {
                spinner2Values[previousSpinner2Index][i]
            }

            currentBonus[i] = pilotBonus + machineBonus
        }

        previousSpinner1Index = newPosition
        updateAllTitles()
        updateButtonAppearance()
    }

    // 機体選択変更時の処理
    private fun onSpinner2Changed(newPosition: Int) {
        val spinner1Values = SpinnerData.getSpinner1Values(currentMode)
        val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(currentMode)
        val spinner2Values = SpinnerData.getSpinner2Values(currentMode)
        val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(currentMode)

        // 前の選択値を減算
        subtractValuesFromNumbers(spinner2Values[previousSpinner2Index])
        if (isBoost2Active) {
            subtractValuesFromNumbers(spinner2BoostValues[previousSpinner2Index])
        }

        // 新しい選択値を加算
        addValuesToNumbers(spinner2Values[newPosition])
        if (isBoost2Active) {
            addValuesToNumbers(spinner2BoostValues[newPosition])
        }

        // ★ currentBonus を更新（パイロット + 機体）
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val pilotBonus = if (isBoost1Active) {
                spinner1BoostValues[previousSpinner1Index][i]
            } else {
                spinner1Values[previousSpinner1Index][i]
            }

            val machineBonus = if (isBoost2Active) {
                spinner2BoostValues[newPosition][i]
            } else {
                spinner2Values[newPosition][i]
            }

            currentBonus[i] = pilotBonus + machineBonus
        }

        previousSpinner2Index = newPosition
        updateAllTitles()
        updateButtonAppearance()
    }

    // 値を加算してUIを更新
    private fun addValuesToNumbers(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbers[i] += values[i]
            numberTexts[i].text = currentNumbers[i].toString()
        }
    }

    // 値を減算してUIを更新
    private fun subtractValuesFromNumbers(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbers[i] -= values[i]
            numberTexts[i].text = currentNumbers[i].toString()
        }
    }

    // すべてのタイトルを更新（ボーナス値を表示）
    private fun updateAllTitles() {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val spinner1Values = SpinnerData.getSpinner1Values(currentMode)
            val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(currentMode)
            val spinner2Values = SpinnerData.getSpinner2Values(currentMode)
            val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(currentMode)

            val pilotBonus = if (isBoost1Active) {
                spinner1BoostValues[previousSpinner1Index][i]
            } else {
                spinner1Values[previousSpinner1Index][i]
            }

            val machineBonus = if (isBoost2Active) {
                spinner2BoostValues[previousSpinner2Index][i]
            } else {
                spinner2Values[previousSpinner2Index][i]
            }

            // ★ パイロット値と機体値を分けて表示
            titleTexts[i].text = getString(
                R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], pilotBonus, machineBonus
            )
        }

        // 4〜6番目のカウンターはボーナスなし
        for (i in SPINNER_AFFECTED_COUNT until COUNTER_COUNT) {
            titleTexts[i].text = SpinnerData.COUNTER_TITLES[i]
        }
    }

    // 現在の状態をSharedPreferencesに保存
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

    // SharedPreferencesから状態を復元
    private fun restoreState() {
        val prefs = getSharedPreferences("eques_state", MODE_PRIVATE)
        currentMode = prefs.getString("MODE", "mode1") ?: "mode1"

        val spinner1Index = prefs.getInt("SPINNER1_INDEX", 0)
        val spinner2Index = prefs.getInt("SPINNER2_INDEX", 0)

        // カウンター値を復元
        for (i in 0 until COUNTER_COUNT) {
            currentNumbers[i] = prefs.getInt("NUMBER_$i", 0)
        }
        // ボーナス値を復元
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentBonus[i] = prefs.getInt("BONUS_$i", 0)
        }

        // ブースト状態を復元
        isBoost1Active = prefs.getBoolean("BOOST1_ACTIVE", false)
        isBoost2Active = prefs.getBoolean("BOOST2_ACTIVE", false)

        // UIを更新
        for (i in 0 until COUNTER_COUNT) {
            numberTexts[i].text = currentNumbers[i].toString()
        }

        // リスナーが呼ばれないように false に設定
        spinner1.setSelection(spinner1Index, false)
        spinner2.setSelection(spinner2Index, false)

        updateButtonAppearance()
        updateAllTitles()
    }
}
