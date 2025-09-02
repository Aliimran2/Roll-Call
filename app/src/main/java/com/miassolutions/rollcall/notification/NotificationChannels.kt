package com.miassolutions.rollcall.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.miassolutions.rollcall.R

object NotificationChannels {

    const val GENERAL_CHANNEL_ID = "general_channel"

    fun createChannels(context: Context) {
        // for API level 26+

        val channel = NotificationChannel(
            GENERAL_CHANNEL_ID,
            context.getString(R.string.general_notifications),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.channel_description)
            enableVibration(true)

        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)


    }
}