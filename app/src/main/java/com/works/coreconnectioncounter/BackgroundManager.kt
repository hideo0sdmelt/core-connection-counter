package com.works.coreconnectioncounter

import android.view.View
import android.widget.ImageView

class BackgroundManager {
    private var topImage: ImageView? = null
    private var bottomImage: ImageView? = null

    fun attach(top: ImageView, bottom: ImageView) {
        topImage = top
        bottomImage = bottom
    }

    fun updateTop(mode: String, spinner1Index: Int) {
        val imageResId = SpinnerData.getPilotBackgroundImage(mode, spinner1Index)
        topImage?.let { img ->
            if (imageResId != 0) {
                img.setImageResource(imageResId)
                img.visibility = View.VISIBLE
            } else {
                img.visibility = View.GONE
            }
        }
    }

    fun updateBottom(mode: String, spinner2Index: Int) {
        val imageResId = SpinnerData.getMechaBackgroundImage(mode, spinner2Index)
        bottomImage?.let { img ->
            if (imageResId != 0) {
                img.setImageResource(imageResId)
                img.visibility = View.VISIBLE
            } else {
                img.visibility = View.GONE
            }
        }
    }

    fun updateBoth(mode: String, spinner1Index: Int, spinner2Index: Int) {
        updateTop(mode, spinner1Index)
        updateBottom(mode, spinner2Index)
    }
}
