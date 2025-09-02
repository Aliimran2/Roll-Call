package com.miassolutions.rollcall.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
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



    private fun getMimeType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "")
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(extension.lowercase()) ?: "*/*"
    }

    /**
     * SAF (MediaStore) approach to copy file to Downloads
     */
    private fun copyAssetToDownloadsSAF(assetFileName: String): Uri? {
        return try {
            val mimeType = getMimeType(assetFileName)
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, assetFileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val resolver = context.contentResolver
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                TODO("VERSION.SDK_INT < Q")
            }

            uri?.let {
                context.assets.open(assetFileName).use { input ->
                    resolver.openOutputStream(it)?.use { output ->
                        input.copyTo(output)
                    }
                }
            }

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Copy file from assets to Downloads using SAF (MediaStore) and show notification
     */
    fun notifyFileFromAssets(assetFileName: String, notificationId: Int = 20001) {
        val fileUri = copyAssetToDownloadsSAF(assetFileName) ?: return

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, getMimeType(assetFileName))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val pendingIntent = createActivityIntent(intent)

        createNotification(
            contentTitle = "File Saved",
            contentText = "Tap to open $assetFileName",
            iconResId =android.R.drawable.arrow_down_float,
            contentIntent = pendingIntent,
            notificationId = notificationId
        )
    }


}