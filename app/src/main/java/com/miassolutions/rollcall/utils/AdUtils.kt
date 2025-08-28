package com.miassolutions.rollcall.utils

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

fun loadBannerAd(
    context: Context,
    container: ViewGroup,
    adUnitId: String,
    adWidth: Int = 360
): AdView {
    val adView = AdView(context)
    adView.adUnitId = adUnitId
    adView.setAdSize(
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    )

    container.removeAllViews()
    container.addView(adView)

    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)

    return adView
}