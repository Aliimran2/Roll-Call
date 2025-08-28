package com.miassolutions.rollcall.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.miassolutions.rollcall.R

class NotificationHelper(private val context: Context) {

    private val channelId = "download_channel"
    private val notificationId = 101

    init {
        createNotificationChannel()
    }


    fun showDownloadCompleteNotification(fileUri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                fileUri,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_bars)
            .setContentTitle("Download Complete")
            .setContentText("$fileName saved to Downloads")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }

    }


    private fun createNotificationChannel() {
        val name = "Download Notifications"
        val descriptionText = "Notifies when file is saved to downloads"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}