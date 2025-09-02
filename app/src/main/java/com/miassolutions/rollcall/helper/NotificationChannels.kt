package com.miassolutions.rollcall.helper


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {

    const val GENERAL_CHANNEL_ID = "general_channel"

    fun createChannels(context: Context) {
        // for API level 26+

        val channel = NotificationChannel(
            GENERAL_CHANNEL_ID,
            "General Notification",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "This is "
            enableVibration(true)

        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)


    }
}