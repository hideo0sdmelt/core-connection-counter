package com.nankaworks.cryptomessage

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.nankaworks.cryptomessage.exceptions.CreateQRException
import com.nankaworks.cryptomessage.utils.ActionGuard
import com.nankaworks.cryptomessage.utils.AdConfirmDialogUtils
import java.util.EnumMap
import kotlin.collections.set

class CreateQRActivity : AppCompatActivity() {
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var inputText: TextInputEditText
    private lateinit var inputKey: TextInputEditText
    private lateinit var createButton: Button
    private lateinit var qrImageView: ImageView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var keyInputLayout: TextInputLayout
    private lateinit var bannerAdManager: BannerAdManager
    private lateinit var scrollView: ScrollView
    private var qrBitmap: Bitmap? = null
    private lateinit var rewardedAdHandler: RewardedAdHandler

    // 連打ガード（多重処理/多重ダイアログ防止）。tryAcquire() 成功時のみ処理開始、終了時 release()
    private val createActionGuard = ActionGuard()
    private var qrDialog: AlertDialog? = null

    companion object {
        private const val MAX_TEXT_LENGTH = 300
        private const val MAX_KEY_LENGTH = 50
        private const val LOGO_SCALE_DOWN_SIZE = 0.15f // QRに対して縦横に15%程度ロゴを縮小する
        private const val QR_CODE_SIZE = 512
        private const val REWARDED_AD_PROBABILITY = 0.25
        private const val SCROLL_OFFSET = 100
        private const val QR_MARGIN = 0
        private const val BITMAP_COMPRESS_QUALITY = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qr)

        // ホームボタンの設定
        findViewById<LinearLayout>(R.id.homeButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.homeImageButton).setOnClickListener { finish() }

        // バナー広告表示
        bannerAdManager = BannerAdManager(this).apply {
            initializeAd(findViewById(R.id.adViewContainer), Constants.BANNER_AD_ID)
        }

        initializeViews()
        setupListeners()

        // リワード広告
        rewardedAdHandler = RewardedAdHandler(this).apply { loadRewardedAd(Constants.REWARD_AD_ID) }
    }

    private fun initializeViews() {
        constraintLayout = findViewById(R.id.constraintLayoutView)
        inputText = findViewById(R.id.inputText)
        inputKey = findViewById(R.id.inputKey)
        createButton = findViewById(R.id.createButton)
        qrImageView = findViewById(R.id.qrImageView)
        textInputLayout = findViewById(R.id.textInputLayout)
        keyInputLayout = findViewById(R.id.keyInputLayout)
        scrollView = findViewById(R.id.createQrScrollView)
    }

    private fun setupListeners() {
        // レイアウト全体範囲クリック時
        constraintLayout.setOnClickListener {
            // キーボードを閉じる
            closeKeyboard()

            // 入力フォームからフォーカスを外す
            inputText.clearFocus()
            inputKey.clearFocus()
        }

        // 作成ボタンクリック時
        createButton.setOnClickListener {
            // 連打ガード: 最初の1回だけ通す
            if (!createActionGuard.tryAcquire()) return@setOnClickListener
            createButton.isEnabled = false

            // リワード広告をロード
            rewardedAdHandler.loadRewardedAd(Constants.REWARD_AD_ID)

            // バリデーションチェック
            if (!validateInputs()) {
                // 入力エラー時は即解除
                createActionGuard.release()
                createButton.isEnabled = true
                return@setOnClickListener
            }

            // キーボード閉じる
            closeKeyboard()

            // QRコード作成
            createQRCodeWithLogo()

            // 広告の準備が整っていたら広告を流す
            if (rewardedAdHandler.isAdLoaded()) {
                // 広告表示の確認ダイアログを表示
                showRewardedAdConfirmDialog()
            } else {
                showQRCodeDialog()
            }
        }

        // 入力欄のフォーカス変化時
        inputText.setOnFocusChangeListener { _, _ ->
            rewardedAdHandler.loadRewardedAd(Constants.REWARD_AD_ID)
        }
        inputKey.setOnFocusChangeListener { _, hasFocus ->
            rewardedAdHandler.loadRewardedAd(Constants.REWARD_AD_ID)
            if (hasFocus) {
                // 暗号化キーの入力欄にフォーカスが当たった時にスクロール
                scrollView.post {
                    // QRコード作成ボタンが見える位置まで
                    val targetY = createButton.top - SCROLL_OFFSET
                    scrollView.scrollTo(0, targetY)
                }
            }
        }

    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (inputText.text.isNullOrBlank()) {
            textInputLayout.error = "テキストを入力してください"
            isValid = false
        } else if ((inputText.text?.length ?: 0) > MAX_TEXT_LENGTH) {
            textInputLayout.error = "テキストは${MAX_TEXT_LENGTH}文字以内で入力してください"
            isValid = false
        } else {
            textInputLayout.error = null
        }

        if (inputKey.text.isNullOrBlank()) {
            keyInputLayout.error = "キーを入力してください"
            isValid = false
        } else if ((inputKey.text?.length ?: 0) > MAX_KEY_LENGTH) {
            keyInputLayout.error = "キーは${MAX_KEY_LENGTH}文字以内で入力してください"
            isValid = false
        } else {
            keyInputLayout.error = null
        }

        return isValid
    }

    /**
     * 広告表示の確認ダイアログを表示
     */
    private fun showRewardedAdConfirmDialog() {
        AdConfirmDialogUtils.showRewardedAdConfirmDialog(
            context = this,
            "QRコードを作っている間、広告が流れることがあります。",
            onConfirm = {
                if (Math.random() < REWARDED_AD_PROBABILITY) {
                    rewardedAdHandler.showRewardedAd(
                        onUserEarnedRewardListener = { rewardItem ->
                            rewardItem.amount
                            rewardItem.type
                        },
                        onDismissed = {
                            // QRコード表示
                            showQRCodeDialog()
                        }
                    )
                } else {
                    showQRCodeDialog()
                }
            },
            onCancel = {
                // 広告ダイアログのキャンセル時も解除
                createActionGuard.release()
                createButton.isEnabled = true
            }
        )
    }

    private fun createQRCodeWithLogo() {
        try {
            // 入力されたテキストとキーを使って暗号化
            val encryptedText = CryptoManager.encrypt(inputText.text.toString(), inputKey.text.toString())

            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                // 誤り訂正レベルLowで、15%程度QRコードが隠れていても読める
                this[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
                // 生成するQRコードの余白を定数で設定
                this[EncodeHintType.MARGIN] = QR_MARGIN
            }

            // QRコードを生成
            val qr = BarcodeEncoder().encodeBitmap(
                encryptedText, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints
            )

            // QRコードを描画するためのビットマップを作成
            qrBitmap = qr.getConfig()?.let { Bitmap.createBitmap(qr.getWidth(), qr.getHeight(), it) }
            val canvas = Canvas(qrBitmap!!)
            canvas.drawBitmap(qr, Matrix(), null) // QRコードをキャンバスに描画

            // ロゴを取得し、QRコードの中央に配置するための準備
            val logo = (ContextCompat.getDrawable(this, R.drawable.logo) as BitmapDrawable?)!!.bitmap
            logo.density = qr.density // 密度を揃える（揃えないとロゴ縮小の操作時に比率が異なり、想定外の挙動をする）

            // QRに対して縦横にロゴを縮小する
            val scaledLogoWidth = (qr.width * LOGO_SCALE_DOWN_SIZE).toInt()
            val scaledLogoHeight = (qr.height * LOGO_SCALE_DOWN_SIZE).toInt()
            val scaledLogo = Bitmap.createScaledBitmap(logo, scaledLogoWidth, scaledLogoHeight, true)

            // ロゴをQRコードの中央に配置するための座標を計算
            val cx = (qr.width - scaledLogoWidth) / 2.0f
            val cy = (qr.height - scaledLogoHeight) / 2.0f

            canvas.drawBitmap(scaledLogo, cx, cy, null) // ロゴをキャンバスに描画

        } catch (e: Exception) {
            Toast.makeText(this, "QRコードの生成に失敗しました", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            throw CreateQRException(e.message)
        }
    }

    private fun showQRCodeDialog() {
        if (qrDialog?.isShowing == true) return

        qrBitmap?.let { bitmap ->
            qrDialog = MaterialAlertDialogBuilder(this)
                .setTitle("QRコード作成結果")
                .setMessage("QRコードが作成されました。\n保存しますか？")
                .setView(ImageView(this).apply {
                    setImageBitmap(bitmap)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                })
                .setPositiveButton("保存") { _, _ ->
                    saveQRCode()
                }
                .setNeutralButton("キャンセル", null)
                .setOnDismissListener {
                    // ダイアログ閉時にガード解除
                    qrDialog = null
                    createActionGuard.release()
                    createButton.isEnabled = true
                }
                .setOnCancelListener {
                    // 外タップやバックキーで閉じた場合も同様に解除
                    qrDialog = null
                    createActionGuard.release()
                    createButton.isEnabled = true
                }
                .create()
            qrDialog?.show()
        } ?: run {
            // 生成失敗時の保険としてガード解除
            createActionGuard.release()
            createButton.isEnabled = true
        }
    }

    private fun saveQRCode() {
        try {
            qrBitmap?.let { bitmap ->
                // MediaStoreを使用してQRコードを保存
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "QR_${System.currentTimeMillis()}.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/みんなのAnngou")
                }

                // MediaStoreに新しいエントリを挿入し、URIを取得
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                uri?.let {
                    // 取得したURIに対して出力ストリームを開く
                    contentResolver.openOutputStream(it).use { outputStream ->
                        outputStream?.let { it1 -> bitmap.compress(Bitmap.CompressFormat.PNG, BITMAP_COMPRESS_QUALITY, it1) }
                    }
                    Toast.makeText(this, "QRコードを保存しました", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // 保存に失敗した場合のエラーメッセージを表示
            Toast.makeText(this, "保存に失敗しました", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    /**
     * QRコード作成開始時にキーボードを閉じる
     */
    private fun closeKeyboard() {
        val view = this.currentFocus
        view?.let {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
