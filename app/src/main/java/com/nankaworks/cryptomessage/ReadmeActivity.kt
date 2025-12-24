package com.nankaworks.cryptomessage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


class ReadmeActivity : AppCompatActivity() {

    // ViewPager2: スワイプでページを切り替えるためのビューコンポーネント
    private lateinit var viewPager: ViewPager2

    // DotsIndicator: 現在のページ位置を示すドットインジケーター
    private lateinit var dotsIndicator: DotsIndicator

    // 「暗号作成」ボタン（画像ボタン）と、コンテナ
    private lateinit var createImageButton: ImageButton
    private lateinit var createButtonContainer: LinearLayout

    // 各ページのデータを保持するデータクラス
    data class Page(
        val title: String,
        val body: String,
        val imageResId: Int
    )

    // アプリ起動時に表示するページデータを定義
    private val pages = listOf(
        Page(
            "1. 合言葉を決めましょう！",
            "暗号を共有したい人同士で、\n共通の合言葉を決めましょう。",
            R.drawable.readme_1_1
        ),
        Page(
            "2. メッセージを暗号化！",
            "メッセージを暗号化するには、\n画面の「暗号作成」をタップ。",
            R.drawable.readme_2_1
        ),
        Page(
            "2. メッセージを暗号化！",
            "上にメッセージ、下に合言葉を入力。\nそして、QRコードを作成をタップ。\nQRコードが作成されたら、保存しましょう。",
            R.drawable.readme_2_2
        ),
        Page(
            "3. 暗号化したメッセージを送る！",
            "チャットアプリなどを使って、\n作成したQRコードを送りましょう。",
            R.drawable.readme_3_1
        ),
        Page(
            "4. 暗号化されたメッセージを読み込む！",
            "QRコードが送られた人は、\n画面の「暗号読込」をタップ。",
            R.drawable.readme_4_1
        ),
        Page(
            "4. 暗号化されたメッセージを読み込む！",
            "合言葉を入力して、\n送られてきた画像を選択。\nもしくは、カメラで画像を撮影しましょう。",
            R.drawable.readme_4_2
        ),
        Page(
            "4. 暗号化されたメッセージを読み込む！",
            "合言葉が合っていたら、\nメッセージを読み込むことができます！",
            R.drawable.readme_4_3
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_readme)

        viewPager = findViewById(R.id.viewPager)
        dotsIndicator = findViewById(R.id.dotsIndicator)
        createImageButton = findViewById(R.id.createImageButton)
        createButtonContainer = findViewById(R.id.createButtonContainer)

        // ViewPager2にアダプターをセット（ページのデータとレイアウトを紐付け）
        viewPager.adapter = ReadmePageAdapter(pages)
        // インジケーターをViewPager2と紐付け（ドットの表示を連動させる）
        dotsIndicator.attachTo(viewPager)

        // 「ホームに戻る」ボタンのクリックリスナーを設定
        findViewById<LinearLayout>(R.id.homeContainer).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.homeImageButton).setOnClickListener { finish() }

        // 「暗号作成」ボタンのクリックリスナーを設定
        // include 側のルートに付けた ID（@id/createButtonContainer）でコンテナを参照
        createButtonContainer.setOnClickListener {
            startActivity(Intent(this, CreateQRActivity::class.java))
        }
        createImageButton.setOnClickListener {
            startActivity(Intent(this, CreateQRActivity::class.java))
        }

        // 最後のスライドが表示されている場合、ボタンを表示
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // 最後のスライドのインデックスを取得
                val lastSlideIndex = viewPager.adapter!!.itemCount - 1

                if (position == lastSlideIndex) {
                    createButtonContainer.visibility = View.VISIBLE
                } else {
                    createButtonContainer.visibility = View.GONE
                }
            }
        })
    }
}
