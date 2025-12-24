package com.nankaworks.cryptomessage.utils

import android.app.Activity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.nankaworks.cryptomessage.R

class NativeAdLoader(private val activity: Activity) {

    fun loadNativeAd(
        adUnitId: String,
        containerId: Int,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailedToLoad: ((LoadAdError) -> Unit)? = null
    ) {
        val adLoader = AdLoader.Builder(activity, adUnitId)
            .forNativeAd { nativeAd: NativeAd ->
                if (activity.isDestroyed) {
                    nativeAd.destroy()
                    return@forNativeAd
                }

                val adView = activity.layoutInflater.inflate(
                    R.layout.native_ad_layout,
                    null
                ) as NativeAdView

                populateNativeAdView(nativeAd, adView)

                val adContainer = activity.findViewById<FrameLayout>(containerId)
                adContainer.removeAllViews()
                adContainer.addView(adView)

                onAdLoaded?.invoke()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    onAdFailedToLoad?.invoke(error)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // ヘッドライン
        adView.findViewById<TextView>(R.id.ad_headline)?.let { headline ->
            headline.text = nativeAd.headline
            adView.headlineView = headline
        }

        // 広告主
        adView.findViewById<TextView>(R.id.ad_advertiser)?.let { advertiser ->
            advertiser.text = nativeAd.advertiser
            adView.advertiserView = advertiser
        }

        // アプリアイコン
        adView.findViewById<ImageView>(R.id.ad_app_icon)?.let { appIcon ->
            nativeAd.icon?.drawable?.let { drawable ->
                appIcon.setImageDrawable(drawable)
            }
            adView.iconView = appIcon
        }

        // 本文
        adView.findViewById<TextView>(R.id.ad_body)?.let { body ->
            body.text = nativeAd.body
            adView.bodyView = body
        }

        // メディアビュー
        adView.findViewById<MediaView>(R.id.ad_media)?.let { mediaView ->
            adView.mediaView = mediaView
        }

        // CTAボタン
        adView.findViewById<Button>(R.id.ad_call_to_action)?.let { cta ->
            cta.text = nativeAd.callToAction
            adView.callToActionView = cta
        }

        adView.setNativeAd(nativeAd)
    }
}
