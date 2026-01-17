package com.example.encryptedrecords

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

object DatabaseExporter {
    fun exportEncryptedDatabase(context: Context): Boolean {
        val sourceFile = DatabaseProvider.getDatabaseFile(context)
        if (!sourceFile.exists()) {
            return false
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "encrypted-records.db")
                    put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream")
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return false
                resolver.openOutputStream(uri)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: return false
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val destination = File(downloadsDir, "encrypted-records.db")
                sourceFile.copyTo(destination, overwrite = true)
            }
            true
        } catch (exception: Exception) {
            false
        }
    }
}
