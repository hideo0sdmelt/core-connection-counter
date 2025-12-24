package com.nankaworks.cryptomessage

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdHandler(private val context: Context) {
    private var rewardedAd: RewardedAd? = null
    private val tag = "RewardedAdHandler"
    private var onAdDismissedCallback: (() -> Unit)? = null

    // 広告がロード済みかどうかをチェックするメソッド
    fun isAdLoaded(): Boolean {
        return rewardedAd != null
    }

    fun loadRewardedAd(adUnitId: String) {

        // すでに広告がロードされている場合は何もしない
        if (isAdLoaded()) {
            Log.d(tag, "Ad is already loaded, skipping load request.")
            return
        }

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(tag, "Ad failed to load: ${adError.message}")
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(tag, "Ad was loaded.")
                rewardedAd = ad

                rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        Log.d(tag, "Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Log.d(tag, "Ad dismissed fullscreen content.")
                        // 広告が閉じられた後にコールバックを実行
                        onAdDismissedCallback?.invoke()
                        rewardedAd = null
                    }

                    override fun onAdImpression() {
                        Log.d(tag, "Ad recorded an impression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(tag, "Ad showed fullscreen content.")
                    }
                }
            }
        })
    }

    fun showRewardedAd(onUserEarnedRewardListener: OnUserEarnedRewardListener, onDismissed: () -> Unit) {
        onAdDismissedCallback = onDismissed
        Log.d(tag, "rewardedAd:$rewardedAd")
        rewardedAd?.show(context as Activity, onUserEarnedRewardListener) ?: run {
            Log.d(tag, "The rewarded ad wasn't ready yet.")
        }
    }
}
