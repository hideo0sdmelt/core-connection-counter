package com.nankaworks.cryptomessage

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {

    private var mInterstitialAd: InterstitialAd? = null
    private val tag = "InterstitialAdManager"// ログ出力用のタグ

    /**
     * インタースティシャル広告読み込み
     */
    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, Constants.INTERSTITIAL_AD_TEST_ID, adRequest, object : InterstitialAdLoadCallback() {

            // 広告読み込み失敗時
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(tag, adError.toString())
                mInterstitialAd = null
            }

            // 広告読み込み成功時
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(tag, "Ad was loaded.")
                mInterstitialAd = interstitialAd

                showInterstitialAd() // 広告がロードされた後に表示
            }
        })
    }

    /**
     * インタースティシャル広告表示
     */
    fun showInterstitialAd() {

        // 広告が読み込まれていない場合はログに出して終了
        if (mInterstitialAd == null) {
            Log.d(tag, "The interstitial ad wasn't ready yet.")
            return
        }

        // 広告表示
        mInterstitialAd?.show(context as Activity)
    }
}
