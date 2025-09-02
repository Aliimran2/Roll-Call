package com.miassolutions.rollcall.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.miassolutions.rollcall.notification.NotificationChannels
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AttendanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this){}
        NotificationChannels.createChannels(this)
    }
}