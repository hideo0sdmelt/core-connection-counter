package com.works.coreconnectioncounter

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {
    private lateinit var buttons: List<ButtonConfig>

    data class ButtonConfig(
        val title: String,
        val navigateTo: Class<*>,
        val bgColor: Int,
        val buttonId: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Toolbarにステータスバー分のパディングを追加
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(
                view.paddingLeft,
                statusBarHeight,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        // ボタンの設定を初期化
        buttons = listOf(
            ButtonConfig("Eques", EquesActivity::class.java, R.color.skyBlue, R.id.createButton),
            ButtonConfig("設定", EquesActivity::class.java, R.color.orange, R.id.button3)
        )

        // タイトル画像の設定
        val titleImage = findViewById<ImageView>(R.id.titleImage)
        titleImage.setImageResource(R.drawable.title)

        // 動的にボタンを生成
        buttons.forEach { buttonConfig ->
            val button = findViewById<MaterialButton>(buttonConfig.buttonId)
            setupAnimatedButton(button, buttonConfig)
        }
    }

    private fun setupAnimatedButton(button: MaterialButton, config: ButtonConfig) {
        with(button) {
            text = config.title
            setBackgroundColor(getColor(config.bgColor))
            setTextColor(getColor(R.color.black))
            setOnClickListener {
                startActivity(Intent(this@HomeActivity, config.navigateTo))
            }
        }
    }
}
