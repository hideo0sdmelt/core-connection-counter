package com.nankaworks.cryptomessage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.nankaworks.cryptomessage.utils.ActionGuard
import com.nankaworks.cryptomessage.utils.AdConfirmDialogUtils
import com.nankaworks.cryptomessage.utils.closeKeyboard
import java.io.File

/**
 * QRを読み取るためのアクティビティ
 */
class ReadQRActivity : AppCompatActivity() {
    private lateinit var cameraRollButton: MaterialButton
    private lateinit var cameraButton: MaterialButton
    private lateinit var inputAreaLayout: TextInputLayout
    private lateinit var bannerAdManager: BannerAdManager
    private lateinit var inputArea: TextInputEditText
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var pictureFile: File
    private var imageUri: Uri? = null
    private lateinit var rewardedAdHandler: RewardedAdHandler
    private val readActionGuard = ActionGuard()
    private var resultDialog: AlertDialog? = null
    private var lastToastTime: Long = 0
    private val toastDebounceDelayMs = 2000L // 2秒

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 101
        const val REQUEST_CAMERA_ROLL_PERMISSION = 102
        const val REWARDED_AD_PROBABILITY = 0.25
    }

    private val getPictureLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) {
            return@registerForActivityResult
        }

        try {
            // 画像からビットマップを取得
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            scanQRCode(bitmap)
        } catch (e: Exception) {
            showToastDebounced("画像の読み込みに失敗しました", Toast.LENGTH_LONG)
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            try {
                // URIからビットマップを取得
                imageUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    scanQRCode(bitmap)
                }
            } catch (e: Exception) {
                showToastDebounced("画像の読み込みに失敗しました", Toast.LENGTH_LONG)
            }
        } else {
            showToastDebounced("写真の撮影がキャンセルされました", Toast.LENGTH_SHORT)
        }
    }

    private fun getPicture() {
        getPictureLauncher.launch("image/*")
    }

    private fun takePicture() {
        try {
            pictureFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                this,
                getString(R.string.file_provider_authority),
                pictureFile
            )
            takePictureLauncher.launch(imageUri)
        } catch (e: Exception) {
            showToastDebounced("カメラの起動に失敗しました", Toast.LENGTH_SHORT)
        }
    }

    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        )
    }

    private fun checkAndRequestPermissions(targetPermission: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                // パーミッションが必要であることの説明を表示
                showToastDebounced("カメラのパーミッションが必要です。設定から許可してください!", Toast.LENGTH_LONG)
            }
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), targetPermission)
            return false
        }
        return true
    }

    /**
     * 連続したトースト表示を防ぐためのデバウンス処理付きトースト表示関数。
     * 指定された遅延時間内に既にトーストが表示されている場合、新たなトーストは表示されない。
     *
     * @param message 表示するメッセージ
     * @param duration トーストの表示時間 (Toast.LENGTH_SHORT または Toast.LENGTH_LONG)
     */
    private fun showToastDebounced(message: String, duration: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastToastTime > toastDebounceDelayMs) {
            Toast.makeText(this, message, duration).show()
            lastToastTime = currentTime
        }
    }

    // ユーザが権限を許可したタイミングで呼ばれる
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // パーミッションが許可されたらカメラを起動
            if (requestCode == REQUEST_CAMERA_PERMISSION) {
                takePicture()
            } else if (requestCode == REQUEST_CAMERA_ROLL_PERMISSION) {
                getPicture()
            }
        } else {
            // パーミッションが拒否された場合
            showToastDebounced(
                "カメラのパーミッションが必要です。設定から許可してください。",
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun showResultDialog(results: String) {
        if (resultDialog?.isShowing == true) return
        resultDialog = MaterialAlertDialogBuilder(this)
            .setTitle("QRコード読み取り結果")
            .setMessage(results)
            .setPositiveButton("コピー") { dialog, _ ->
                // クリップボードにコピー
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("QRコード内容", results)
                clipboard.setPrimaryClip(clip)
                showToastDebounced("クリップボードにコピーしました", Toast.LENGTH_SHORT)
                dialog.dismiss()

                val reviewManager = ReviewManagerFactory.create(this)
                reviewManager.requestReviewFlow()
                    .addOnSuccessListener { reviewInfo ->
                        reviewManager.launchReviewFlow(this, reviewInfo)
                            .addOnCompleteListener { Log.d("ReadQrActivity", "Thanks for review!") }
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            "ReadQrActivity",
                            "Review request failed with error code: ${(e as ReviewException).errorCode}"
                        )
                    }
            }
            .setNeutralButton("キャンセル", null)
            .setOnDismissListener {
                resultDialog = null
                readActionGuard.release()
            }
            .setOnCancelListener {
                resultDialog = null
                readActionGuard.release()
            }
            .create()
        resultDialog?.show()
    }

    private fun scanQRCode(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    showToastDebounced("QRコードが見つかりませんでした", Toast.LENGTH_LONG)
                    return@addOnSuccessListener
                }

                val qrContent = barcodes[0].rawValue
                if (qrContent == null) {
                    showToastDebounced("QRコードの内容が取得できませんでした", Toast.LENGTH_LONG)
                    return@addOnSuccessListener
                }
                Log.d("qrContent", qrContent)

                handleDecryptionAndResult(qrContent)
            }
            .addOnFailureListener { _ ->
                showToastDebounced("QRコードの読み取りに失敗しました", Toast.LENGTH_LONG)
            }
    }

    private fun handleDecryptionAndResult(qrContent: String) {
        try {
            val qrDecrypted = CryptoManager.decrypt(qrContent, inputArea.text.toString())
            if (!readActionGuard.tryAcquire()) {
                return
            }
            if (rewardedAdHandler.isAdLoaded()) {
                showRewardedAdConfirmDialog(qrDecrypted)
            } else {
                showResultDialog(qrDecrypted)
            }
        } catch (e: com.nankaworks.cryptomessage.exceptions.InvalidPasswordException) {
            showToastDebounced("暗号化キーが正しくありません", Toast.LENGTH_LONG)
        } catch (e: com.nankaworks.cryptomessage.exceptions.InvalidQrFormatException) {
            showToastDebounced("復号できないQRコードです", Toast.LENGTH_LONG)
        } catch (e: Exception) {
            showToastDebounced("QRコードの読み取りに失敗しました", Toast.LENGTH_LONG)
        }
    }

    /**
     * 広告表示の確認ダイアログを表示
     */
    private fun showRewardedAdConfirmDialog(qrDecrypted: String) {
        AdConfirmDialogUtils.showRewardedAdConfirmDialog(
            context = this,
            "QRコードを読み込んでいる間、広告が流れることがあります。",
            onConfirm = {
                if (Math.random() < REWARDED_AD_PROBABILITY) {
                    rewardedAdHandler.showRewardedAd(
                        onUserEarnedRewardListener = { rewardItem ->
                            rewardItem.amount
                            rewardItem.type
                        },
                        onDismissed = {
                            // 読み取り結果表示
                            showResultDialog(qrDecrypted)
                        }
                    )
                } else {
                    showResultDialog(qrDecrypted)
                }
            },
            onCancel = {
                readActionGuard.release()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_readqr)

        // 「ホームに戻る」ボタンのクリックリスナーを設定
        findViewById<LinearLayout>(R.id.homeContainer).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.homeImageButton).setOnClickListener { finish() }

        // バナー広告表示
        bannerAdManager = BannerAdManager(this).apply {
            initializeAd(findViewById(R.id.adViewContainer), Constants.BANNER_AD_ID)
        }

        // ビューの初期化
        cameraRollButton = findViewById(R.id.cameraRollButton)
        cameraButton = findViewById(R.id.cameraButton)
        inputAreaLayout = findViewById(R.id.inputAreaLayout)
        inputArea = findViewById(R.id.input_area)

        constraintLayout = findViewById(R.id.constraintLayoutView)
        constraintLayout.setOnClickListener {
            closeKeyboard()
            inputArea.clearFocus()
        }

        // リスナーの設定
        cameraRollButton.setOnClickListener(selectQRListener)
        cameraButton.setOnClickListener(selectQRListener)
        inputArea.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                rewardedAdHandler.loadRewardedAd(Constants.REWARD_AD_ID)
            }
        }

        // リワード広告
        rewardedAdHandler = RewardedAdHandler(this).apply { loadRewardedAd(Constants.REWARD_AD_ID) }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        if (inputArea.text.isNullOrBlank()) {
            inputAreaLayout.error = "暗号化キーを入力してください"
            isValid = false
        } else if ((inputArea.text?.length ?: 0) > 50) {
            inputAreaLayout.error = "キーは50文字以内で入力してください"
            isValid = false
        } else {
            inputAreaLayout.error = null
        }
        return isValid
    }

    private val selectQRListener = View.OnClickListener { v ->
        // キーボードを閉じる
        closeKeyboard()

        // リワード広告を読み込み
        rewardedAdHandler.loadRewardedAd(Constants.REWARD_AD_ID)

        if (validateInput()) {
            if (v == cameraRollButton && checkAndRequestPermissions(REQUEST_CAMERA_ROLL_PERMISSION)) {
                getPicture()
            } else if (v == cameraButton && checkAndRequestPermissions(REQUEST_CAMERA_PERMISSION)) {
                takePicture()
            }
        }
    }
}
