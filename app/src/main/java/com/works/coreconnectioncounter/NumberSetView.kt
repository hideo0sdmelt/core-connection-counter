package com.works.coreconnectioncounter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class NumberSetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val numberText: TextView
    private val titleText: TextView
    private val plusButton: MaterialButton
    private val minusButton: MaterialButton

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_number_set, this, true)
        numberText = findViewById(R.id.numberText)
        titleText = findViewById(R.id.titleText)
        plusButton = findViewById(R.id.plusButton)
        minusButton = findViewById(R.id.minusButton)
    }

    fun setNumber(value: Int) {
        numberText.text = value.toString()
    }

    fun getNumber(): Int = numberText.text.toString().toIntOrNull() ?: 0

    fun setTitle(text: CharSequence) {
        titleText.text = text
    }

    fun setPlusClickListener(listener: OnClickListener) {
        plusButton.setOnClickListener(listener)
    }

    fun setMinusClickListener(listener: OnClickListener) {
        minusButton.setOnClickListener(listener)
    }
}
