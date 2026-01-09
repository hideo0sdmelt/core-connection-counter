package com.works.coreconnectioncounter

import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ThirdActivity : AppCompatActivity() {
    companion object {
        private const val COUNTER_COUNT = 6
        private const val SPINNER_AFFECTED_COUNT = 3
        private const val EP_INDEX = 5
        private const val EP_THRESHOLD = 20
    }

    // Left side
    private val currentNumbersLeft = IntArray(COUNTER_COUNT)
    private val currentBonusLeft = IntArray(SPINNER_AFFECTED_COUNT)
    private var previousSpinner1Left = 0
    private var previousSpinner2Left = 0
    private var isBoost1Left = false
    private var isBoost2Left = false
    private lateinit var numberTextsLeft: Array<TextView>
    private lateinit var titleTextsLeft: Array<TextView>
    private var isFirstSelection1Left = true
    private var isFirstSelection2Left = true

    private lateinit var spinner1Left: Spinner
    private lateinit var spinner2Left: Spinner
    private lateinit var homeButton: MaterialButton
    private lateinit var resetButton: MaterialButton

    // Right side
    private val currentNumbersRight = IntArray(COUNTER_COUNT)
    private val currentBonusRight = IntArray(SPINNER_AFFECTED_COUNT)
    private var previousSpinner1Right = 0
    private var previousSpinner2Right = 0
    private var isBoost1Right = false
    private var isBoost2Right = false
    private lateinit var numberTextsRight: Array<TextView>
    private lateinit var titleTextsRight: Array<TextView>
    private var isFirstSelection1Right = true
    private var isFirstSelection2Right = true

    private lateinit var spinner1Right: Spinner
    private lateinit var spinner2Right: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        setupBackHandler()
        restoreAll()
        setupSpinnersAndCounters()
        setupHomeButton()
        setupResetButton()
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveAll()
                finish()
            }
        })
    }

    private fun setupSpinnersAndCounters() {
        // Left spinners
        val spinnerSet1Left = findViewById<View>(R.id.spinnerSet1_left)
        val spinnerSet2Left = findViewById<View>(R.id.spinnerSet2_left)
        spinner1Left = spinnerSet1Left.findViewById<android.widget.Spinner>(R.id.spinner)
        spinner2Left = spinnerSet2Left.findViewById<android.widget.Spinner>(R.id.spinner)

        val spinnerTitle1Left = spinnerSet1Left.findViewById<TextView>(R.id.spinnerTitle)
        val spinnerTitle2Left = spinnerSet2Left.findViewById<TextView>(R.id.spinnerTitle)

        val managerLeft = SpinnerManager(
            this,
            getMode = { "mode1" },
            isFirstSelection1 = { isFirstSelection1Left },
            setFirstSelection1 = { v -> isFirstSelection1Left = v },
            isFirstSelection2 = { isFirstSelection2Left },
            setFirstSelection2 = { v -> isFirstSelection2Left = v },
            onFirstSelection1 = { position ->
                isFirstSelection1Left = false
                previousSpinner1Left = position

                // 初回選択時の初期加算
                if (position != 0) {
                    val spinner1Values = SpinnerData.getSpinner1Values("mode1")
                    addValuesToNumbersLeft(spinner1Values[position])
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusLeft[i] = spinner1Values[position][i]
                    }
                } else {
                    val spinner1Values = SpinnerData.getSpinner1Values("mode1")
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusLeft[i] = spinner1Values[position][i]
                    }
                }

                for (i in 0 until SPINNER_AFFECTED_COUNT) {
                    titleTextsLeft[i].text = getString(
                        R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusLeft[i]
                    )
                }
            },
            onFirstSelection2 = { position ->
                isFirstSelection2Left = false
                previousSpinner2Left = position

                if (position != 0) {
                    val spinner2Values = SpinnerData.getSpinner2Values("mode1")
                    addValuesToNumbersLeft(spinner2Values[position])
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusLeft[i] += spinner2Values[position][i]
                    }
                } else {
                    val spinner2Values = SpinnerData.getSpinner2Values("mode1")
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusLeft[i] += spinner2Values[position][i]
                    }
                }

                for (i in 0 until SPINNER_AFFECTED_COUNT) {
                    titleTextsLeft[i].text = getString(
                        R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusLeft[i]
                    )
                }
            },
            onSelection1Changed = { position -> onSpinner1LeftChanged(position) },
            onSelection2Changed = { position -> onSpinner2LeftChanged(position) }
        )

        managerLeft.attach(spinner1Left, spinner2Left, spinnerTitle1Left, spinnerTitle2Left, previousSpinner1Left, previousSpinner2Left, false)

        // Right spinners
        val spinnerSet1Right = findViewById<View>(R.id.spinnerSet1_right)
        val spinnerSet2Right = findViewById<View>(R.id.spinnerSet2_right)
        spinner1Right = spinnerSet1Right.findViewById<android.widget.Spinner>(R.id.spinner)
        spinner2Right = spinnerSet2Right.findViewById<android.widget.Spinner>(R.id.spinner)

        val spinnerTitle1Right = spinnerSet1Right.findViewById<TextView>(R.id.spinnerTitle)
        val spinnerTitle2Right = spinnerSet2Right.findViewById<TextView>(R.id.spinnerTitle)

        val managerRight = SpinnerManager(
            this,
            getMode = { "mode1" },
            isFirstSelection1 = { isFirstSelection1Right },
            setFirstSelection1 = { v -> isFirstSelection1Right = v },
            isFirstSelection2 = { isFirstSelection2Right },
            setFirstSelection2 = { v -> isFirstSelection2Right = v },
            onFirstSelection1 = { position ->
                isFirstSelection1Right = false
                previousSpinner1Right = position
                if (position != 0) {
                    val spinner1Values = SpinnerData.getSpinner1Values("mode1")
                    addValuesToNumbersRight(spinner1Values[position])
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusRight[i] = spinner1Values[position][i]
                    }
                } else {
                    val spinner1Values = SpinnerData.getSpinner1Values("mode1")
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusRight[i] = spinner1Values[position][i]
                    }
                }
                for (i in 0 until SPINNER_AFFECTED_COUNT) {
                    titleTextsRight[i].text = getString(
                        R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusRight[i]
                    )
                }
            },
            onFirstSelection2 = { position ->
                isFirstSelection2Right = false
                previousSpinner2Right = position
                if (position != 0) {
                    val spinner2Values = SpinnerData.getSpinner2Values("mode1")
                    addValuesToNumbersRight(spinner2Values[position])
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusRight[i] += spinner2Values[position][i]
                    }
                } else {
                    val spinner2Values = SpinnerData.getSpinner2Values("mode1")
                    for (i in 0 until SPINNER_AFFECTED_COUNT) {
                        currentBonusRight[i] += spinner2Values[position][i]
                    }
                }
                for (i in 0 until SPINNER_AFFECTED_COUNT) {
                    titleTextsRight[i].text = getString(
                        R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusRight[i]
                    )
                }
            },
            onSelection1Changed = { position -> onSpinner1RightChanged(position) },
            onSelection2Changed = { position -> onSpinner2RightChanged(position) }
        )

        managerRight.attach(spinner1Right, spinner2Right, spinnerTitle1Right, spinnerTitle2Right, previousSpinner1Right, previousSpinner2Right, false)

        // Counters: prepare arrays and listeners
        numberTextsLeft = Array(COUNTER_COUNT) { i ->
            val leftId = resources.getIdentifier("numberSet${i+1}_left", "id", packageName)
            findViewById<View>(leftId).findViewById(R.id.numberText)
        }
        titleTextsLeft = Array(COUNTER_COUNT) { i ->
            val leftId = resources.getIdentifier("numberSet${i+1}_left", "id", packageName)
            findViewById<View>(leftId).findViewById(R.id.titleText)
        }

        numberTextsRight = Array(COUNTER_COUNT) { i ->
            val rightId = resources.getIdentifier("numberSet${i+1}_right", "id", packageName)
            findViewById<View>(rightId).findViewById(R.id.numberText)
        }
        titleTextsRight = Array(COUNTER_COUNT) { i ->
            val rightId = resources.getIdentifier("numberSet${i+1}_right", "id", packageName)
            findViewById<View>(rightId).findViewById(R.id.titleText)
        }

        // initialize titles from restored bonuses
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            titleTextsLeft[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusLeft[i])
            titleTextsRight[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusRight[i])
        }
        for (i in SPINNER_AFFECTED_COUNT until COUNTER_COUNT) {
            titleTextsLeft[i].text = SpinnerData.COUNTER_TITLES[i]
            titleTextsRight[i].text = SpinnerData.COUNTER_TITLES[i]
        }

        for (i in 0 until COUNTER_COUNT) {
            val leftId = resources.getIdentifier("numberSet${i+1}_left", "id", packageName)
            val rightId = resources.getIdentifier("numberSet${i+1}_right", "id", packageName)
            val leftView = findViewById<View>(leftId)
            val rightView = findViewById<View>(rightId)

            val leftPlus = leftView.findViewById<com.google.android.material.button.MaterialButton>(R.id.plusButton)
            val leftMinus = leftView.findViewById<com.google.android.material.button.MaterialButton>(R.id.minusButton)
            val rightPlus = rightView.findViewById<com.google.android.material.button.MaterialButton>(R.id.plusButton)
            val rightMinus = rightView.findViewById<com.google.android.material.button.MaterialButton>(R.id.minusButton)

            // Left listeners
            numberTextsLeft[i].text = currentNumbersLeft[i].toString()
            leftPlus.setOnClickListener {
                val old = currentNumbersLeft[i]
                currentNumbersLeft[i]++
                numberTextsLeft[i].text = currentNumbersLeft[i].toString()
                if (i == EP_INDEX) checkEpThresholdLeft(old, currentNumbersLeft[i])
            }
            leftMinus.setOnClickListener {
                val old = currentNumbersLeft[i]
                currentNumbersLeft[i]--
                numberTextsLeft[i].text = currentNumbersLeft[i].toString()
                if (i == EP_INDEX) checkEpThresholdLeft(old, currentNumbersLeft[i])
            }

            // Right listeners
            numberTextsRight[i].text = currentNumbersRight[i].toString()
            rightPlus.setOnClickListener {
                val old = currentNumbersRight[i]
                currentNumbersRight[i]++
                numberTextsRight[i].text = currentNumbersRight[i].toString()
                if (i == EP_INDEX) checkEpThresholdRight(old, currentNumbersRight[i])
            }
            rightMinus.setOnClickListener {
                val old = currentNumbersRight[i]
                currentNumbersRight[i]--
                numberTextsRight[i].text = currentNumbersRight[i].toString()
                if (i == EP_INDEX) checkEpThresholdRight(old, currentNumbersRight[i])
            }
        }
    }

    private fun onSpinner1LeftChanged(newPosition: Int) {
        val mode = "mode1"
        val spinner1Values = SpinnerData.getSpinner1Values(mode)
        val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(mode)
        val spinner2Values = SpinnerData.getSpinner2Values(mode)
        val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(mode)

        // subtract previous
        subtractValuesFromNumbersLeft(spinner1Values[previousSpinner1Left])
        if (isBoost1Left) subtractValuesFromNumbersLeft(spinner1BoostValues[previousSpinner1Left])

        // add new
        addValuesToNumbersLeft(spinner1Values[newPosition])
        if (isBoost1Left) addValuesToNumbersLeft(spinner1BoostValues[newPosition])

        // update bonuses and titles
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val pilotBonus = if (isBoost1Left) spinner1BoostValues[newPosition][i] else spinner1Values[newPosition][i]
            val machineBonus = if (isBoost2Left) spinner2BoostValues[previousSpinner2Left][i] else spinner2Values[previousSpinner2Left][i]
            currentBonusLeft[i] = pilotBonus + machineBonus
            titleTextsLeft[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], pilotBonus, machineBonus)
        }

        previousSpinner1Left = newPosition
    }

    private fun onSpinner2LeftChanged(newPosition: Int) {
        val mode = "mode1"
        val spinner1Values = SpinnerData.getSpinner1Values(mode)
        val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(mode)
        val spinner2Values = SpinnerData.getSpinner2Values(mode)
        val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(mode)

        // subtract previous
        subtractValuesFromNumbersLeft(spinner2Values[previousSpinner2Left])
        if (isBoost2Left) subtractValuesFromNumbersLeft(spinner2BoostValues[previousSpinner2Left])

        // add new
        addValuesToNumbersLeft(spinner2Values[newPosition])
        if (isBoost2Left) addValuesToNumbersLeft(spinner2BoostValues[newPosition])

        // update bonuses and titles
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val pilotBonus = if (isBoost1Left) spinner1BoostValues[previousSpinner1Left][i] else spinner1Values[previousSpinner1Left][i]
            val machineBonus = if (isBoost2Left) spinner2BoostValues[newPosition][i] else spinner2Values[newPosition][i]
            currentBonusLeft[i] = pilotBonus + machineBonus
            titleTextsLeft[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], pilotBonus, machineBonus)
        }

        previousSpinner2Left = newPosition
    }

    private fun onSpinner1RightChanged(newPosition: Int) {
        val mode = "mode1"
        val spinner1Values = SpinnerData.getSpinner1Values(mode)
        val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(mode)
        val spinner2Values = SpinnerData.getSpinner2Values(mode)
        val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(mode)

        subtractValuesFromNumbersRight(spinner1Values[previousSpinner1Right])
        if (isBoost1Right) subtractValuesFromNumbersRight(spinner1BoostValues[previousSpinner1Right])

        addValuesToNumbersRight(spinner1Values[newPosition])
        if (isBoost1Right) addValuesToNumbersRight(spinner1BoostValues[newPosition])

        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val pilotBonus = if (isBoost1Right) spinner1BoostValues[newPosition][i] else spinner1Values[newPosition][i]
            val machineBonus = if (isBoost2Right) spinner2BoostValues[previousSpinner2Right][i] else spinner2Values[previousSpinner2Right][i]
            currentBonusRight[i] = pilotBonus + machineBonus
            titleTextsRight[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], pilotBonus, machineBonus)
        }

        previousSpinner1Right = newPosition
    }

    private fun onSpinner2RightChanged(newPosition: Int) {
        val mode = "mode1"
        val spinner1Values = SpinnerData.getSpinner1Values(mode)
        val spinner1BoostValues = SpinnerData.getSpinner1BoostValues(mode)
        val spinner2Values = SpinnerData.getSpinner2Values(mode)
        val spinner2BoostValues = SpinnerData.getSpinner2BoostValues(mode)

        subtractValuesFromNumbersRight(spinner2Values[previousSpinner2Right])
        if (isBoost2Right) subtractValuesFromNumbersRight(spinner2BoostValues[previousSpinner2Right])

        addValuesToNumbersRight(spinner2Values[newPosition])
        if (isBoost2Right) addValuesToNumbersRight(spinner2BoostValues[newPosition])

        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            val pilotBonus = if (isBoost1Right) spinner1BoostValues[previousSpinner1Right][i] else spinner1Values[previousSpinner1Right][i]
            val machineBonus = if (isBoost2Right) spinner2BoostValues[newPosition][i] else spinner2Values[newPosition][i]
            currentBonusRight[i] = pilotBonus + machineBonus
            titleTextsRight[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], pilotBonus, machineBonus)
        }

        previousSpinner2Right = newPosition
    }

    private fun checkEpThresholdLeft(oldValue: Int, newValue: Int) {
        if (oldValue < EP_THRESHOLD && newValue >= EP_THRESHOLD) {
            Toast.makeText(this, "左: EPが${EP_THRESHOLD}に達しました！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEpThresholdRight(oldValue: Int, newValue: Int) {
        if (oldValue < EP_THRESHOLD && newValue >= EP_THRESHOLD) {
            Toast.makeText(this, "右: EPが${EP_THRESHOLD}に達しました！", Toast.LENGTH_SHORT).show()
        }
    }

    // helpers for left/right add/subtract
    private fun addValuesToNumbersLeft(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbersLeft[i] += values[i]
            numberTextsLeft[i].text = currentNumbersLeft[i].toString()
        }
    }

    private fun subtractValuesFromNumbersLeft(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbersLeft[i] -= values[i]
            numberTextsLeft[i].text = currentNumbersLeft[i].toString()
        }
    }

    private fun addValuesToNumbersRight(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbersRight[i] += values[i]
            numberTextsRight[i].text = currentNumbersRight[i].toString()
        }
    }

    private fun subtractValuesFromNumbersRight(values: IntArray) {
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentNumbersRight[i] -= values[i]
            numberTextsRight[i].text = currentNumbersRight[i].toString()
        }
    }

    private fun saveAll() {
        PreferencesHelper(this, "eques_state_third_left").save(
            CounterState(
                mode = "mode1",
                spinner1Index = previousSpinner1Left,
                spinner2Index = previousSpinner2Left,
                numbers = currentNumbersLeft.copyOf(),
                bonuses = currentBonusLeft.copyOf(),
                boost1Active = isBoost1Left,
                boost2Active = isBoost2Left
            )
        )
        PreferencesHelper(this, "eques_state_third_right").save(
            CounterState(
                mode = "mode1",
                spinner1Index = previousSpinner1Right,
                spinner2Index = previousSpinner2Right,
                numbers = currentNumbersRight.copyOf(),
                bonuses = currentBonusRight.copyOf(),
                boost1Active = isBoost1Right,
                boost2Active = isBoost2Right
            )
        )
    }

    private fun setupHomeButton() {
        homeButton = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {
            saveAll()
            finish()
        }
    }

    private fun setupResetButton() {
        resetButton = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("リセット確認")
                .setMessage("左右ともに値を初期化します。よろしいですか？")
                .setPositiveButton("OK") { _, _ ->
                    resetThirdValues()
                }
                .setNegativeButton("キャンセル", null)
                .show()
        }
    }

    private fun resetThirdValues() {
        // clear prefs
        PreferencesHelper(this, "eques_state_third_left").clear()
        PreferencesHelper(this, "eques_state_third_right").clear()

        // reset numbers and UI
        for (i in 0 until COUNTER_COUNT) {
            currentNumbersLeft[i] = 0
            currentNumbersRight[i] = 0
            numberTextsLeft[i].text = "0"
            numberTextsRight[i].text = "0"
        }
        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentBonusLeft[i] = 0
            currentBonusRight[i] = 0
        }

        isBoost1Left = false
        isBoost2Left = false
        isBoost1Right = false
        isBoost2Right = false

        // reset spinners without triggering listeners
        spinner1Left.setSelection(0, false)
        spinner2Left.setSelection(0, false)
        spinner1Right.setSelection(0, false)
        spinner2Right.setSelection(0, false)

        // apply initial default spinner values
        val spinner1Values = SpinnerData.getSpinner1Values("mode1")
        val spinner2Values = SpinnerData.getSpinner2Values("mode1")

        addValuesToNumbersLeft(spinner1Values[0])
        addValuesToNumbersLeft(spinner2Values[0])
        addValuesToNumbersRight(spinner1Values[0])
        addValuesToNumbersRight(spinner2Values[0])

        previousSpinner1Left = 0
        previousSpinner2Left = 0
        previousSpinner1Right = 0
        previousSpinner2Right = 0

        for (i in 0 until SPINNER_AFFECTED_COUNT) {
            currentBonusLeft[i] = spinner1Values[0][i] + spinner2Values[0][i]
            currentBonusRight[i] = spinner1Values[0][i] + spinner2Values[0][i]
            titleTextsLeft[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusLeft[i])
            titleTextsRight[i].text = getString(R.string.title_with_bonus, SpinnerData.COUNTER_TITLES[i], 0, currentBonusRight[i])
        }

        for (i in SPINNER_AFFECTED_COUNT until COUNTER_COUNT) {
            titleTextsLeft[i].text = SpinnerData.COUNTER_TITLES[i]
            titleTextsRight[i].text = SpinnerData.COUNTER_TITLES[i]
        }
    }

    private fun restoreAll() {
        val left = PreferencesHelper(this, "eques_state_third_left").restore()
        for (i in 0 until COUNTER_COUNT) currentNumbersLeft[i] = left.numbers.getOrElse(i) { 0 }
        for (i in 0 until SPINNER_AFFECTED_COUNT) currentBonusLeft[i] = left.bonuses.getOrElse(i) { 0 }
        previousSpinner1Left = left.spinner1Index
        previousSpinner2Left = left.spinner2Index
        isBoost1Left = left.boost1Active
        isBoost2Left = left.boost2Active

        val right = PreferencesHelper(this, "eques_state_third_right").restore()
        for (i in 0 until COUNTER_COUNT) currentNumbersRight[i] = right.numbers.getOrElse(i) { 0 }
        for (i in 0 until SPINNER_AFFECTED_COUNT) currentBonusRight[i] = right.bonuses.getOrElse(i) { 0 }
        previousSpinner1Right = right.spinner1Index
        previousSpinner2Right = right.spinner2Index
        isBoost1Right = right.boost1Active
        isBoost2Right = right.boost2Active
        // mark that we've restored initial selection state
        isFirstSelection1Left = false
        isFirstSelection2Left = false
        isFirstSelection1Right = false
        isFirstSelection2Right = false
    }
}
