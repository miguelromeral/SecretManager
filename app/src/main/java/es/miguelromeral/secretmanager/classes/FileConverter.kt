package es.miguelromeral.secretmanager.classes

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log

class FileConverter {

    fun readTextFromUri(uri: Uri, context: Context): ByteArray? {
        try {
            val contentResolver = context.contentResolver
            return contentResolver.openInputStream(uri)?.readBytes()
        }catch (e: java.lang.Exception){
            Log.i("FileUtils", "Read Text Problem: ${e.message}")
            return null
        }
    }

}