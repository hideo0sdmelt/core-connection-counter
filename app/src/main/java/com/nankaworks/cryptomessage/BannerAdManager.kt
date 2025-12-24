package com.nankaworks.cryptomessage

import android.content.Context
import android.widget.FrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class BannerAdManager(private val context: Context) {
    private var adView: AdView? = null

    /**
     * バナー広告を初期化
     */
    fun initializeAd(adViewContainer: FrameLayout, adUnitId: String) {
        // AdViewを初期化
        adView = AdView(context).apply {
            this.adUnitId = adUnitId
            this.setAdSize(AdSize.BANNER)
        }

        // AdViewをコンテナに追加
        adViewContainer.removeAllViews()
        adViewContainer.addView(adView)

        // 広告リクエストを作成して読み込む
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    fun getAdView(): AdView? {
        return adView
    }
}
