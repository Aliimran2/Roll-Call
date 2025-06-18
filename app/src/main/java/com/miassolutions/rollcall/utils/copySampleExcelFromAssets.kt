package com.miassolutions.rollcall.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
fun copySampleExcelFromAssets(context: Context, assetFileName: String, targetFileName: String = "sample_students.xlsx") {
    try {
        val resolver = context.contentResolver
        val mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, targetFileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(uri)?.use { outputStream ->
                context.assets.open(assetFileName).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
