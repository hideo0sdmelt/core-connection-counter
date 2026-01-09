package com.works.coreconnectioncounter.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.widget.TextView
import com.google.android.material.button.MaterialButton

object ButtonAnimationUtils {
    private val buttonAnimators = mutableMapOf<MaterialButton, ObjectAnimator>()
    private val textAnimators = mutableMapOf<TextView, AnimatorSet>()

    fun setButtonAwakened(
        button: MaterialButton,
        isAwakened: Boolean
    ) {
        // ✅ 既存のアニメーションをキャンセル
        buttonAnimators[button]?.cancel()

        if (isAwakened) {
            // ✅ 新しいアニメーション開始
            val animator = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.5f, 1f)
            animator.duration = 1500
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.start()
            buttonAnimators[button] = animator
        } else {
            // ✅ アニメーション停止
            button.alpha = 1f
            buttonAnimators.remove(button)
        }
    }

    fun setCounterTextsAnimated(
        counterTexts: Array<TextView>,
        affectedCount: Int,
        isAnimated: Boolean
    ) {
        // 影響を受けるカウンター（近接、狙撃、耐久）にアニメーションを適用
        for (i in 0 until affectedCount) {
            val textView = counterTexts[i]
            textAnimators[textView]?.cancel()

            if (isAnimated) {
                // スケールアニメーション（1.0 → 1.1 → 1.0）
                val animatorX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.1f, 1f)
                val animatorY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.1f, 1f)
                animatorX.duration = 1500
                animatorY.duration = 1500
                animatorX.repeatCount = ObjectAnimator.INFINITE
                animatorY.repeatCount = ObjectAnimator.INFINITE
                animatorX.repeatMode = ObjectAnimator.REVERSE
                animatorY.repeatMode = ObjectAnimator.REVERSE
                
                // 少しずつずらして波のような効果を出す
                val delay = (i * 200).toLong()
                animatorX.startDelay = delay
                animatorY.startDelay = delay
                
                // AnimatorSetで同時に実行
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(animatorX, animatorY)
                animatorSet.start()
                textAnimators[textView] = animatorSet
            } else {
                // アニメーション停止
                textView.scaleX = 1f
                textView.scaleY = 1f
                textAnimators.remove(textView)
            }
        }
    }
}
