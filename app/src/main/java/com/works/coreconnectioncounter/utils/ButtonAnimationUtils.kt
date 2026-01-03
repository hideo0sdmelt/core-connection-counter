package com.works.coreconnectioncounter.utils

import android.animation.ObjectAnimator
import com.google.android.material.button.MaterialButton

object ButtonAnimationUtils {
    private val animators = mutableMapOf<MaterialButton, ObjectAnimator>()

    fun setButtonAwakened(
        button: MaterialButton,
        isAwakened: Boolean
    ) {
        // ✅ 既存のアニメーションをキャンセル
        animators[button]?.cancel()

        if (isAwakened) {
            // ✅ 新しいアニメーション開始
            val animator = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.5f, 1f)
            animator.duration = 1500
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.start()
            animators[button] = animator
        } else {
            // ✅ アニメーション停止
            button.alpha = 1f
            animators.remove(button)
        }
    }
}
