package com.miassolutions.rollcall.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(@ApplicationContext private val context: Context) {


    @SuppressLint("MissingPermission")
    fun createNotification(
        contentTitle: String,
        contentText: String,
        iconResId: Int = android.R.drawable.ic_dialog_info,
        contentIntent: PendingIntent? = null,
        actions: List<NotificationCompat.Action> = emptyList(),
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        notificationId: Int = 10001


    ) {
        val builder = NotificationCompat.Builder(context, NotificationChannels.GENERAL_CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(iconResId)
            .setPriority(priority)
            .setAutoCancel(true)
            .apply {
                contentIntent?.let { setContentIntent(it) }
                actions.forEach { addAction(it) }
            }


        NotificationManagerCompat.from(context).notify(notificationId, builder.build())

    }


    fun createActivityIntent(intent: Intent, requestCode: Int = 0): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT //todo()
        )




}