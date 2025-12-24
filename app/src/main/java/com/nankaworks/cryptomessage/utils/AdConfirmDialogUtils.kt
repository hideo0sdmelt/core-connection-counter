package com.nankaworks.cryptomessage.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 確認ダイアログ表示
 */
object AdConfirmDialogUtils {
    fun showRewardedAdConfirmDialog(
        context: Context,
        confirmationMessage: String,
        onConfirm: () -> Unit,
        onCancel: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("確認")
            .setMessage(confirmationMessage)
            .setPositiveButton("続ける") { _, _ ->
                onConfirm()
            }
            .setNeutralButton("キャンセル") { dialog, _ ->
                dialog.dismiss()
                onCancel()
            }
            .setOnCancelListener {
                // バックキーやモーダル外タップで閉じた場合も同様にキャンセル扱い
                onCancel()
            }
            .show()
    }
}
