package es.miguelromeral.secretmanager.classes

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import android.provider.MediaStore


fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor!!.moveToFirst()
        return cursor!!.getString(column_index)
    } catch (e: Exception) {
        Log.e("FileUtils", "getRealPathFromURI Exception : $e")
        return ""
    } finally {
        if (cursor != null) {
            cursor!!.close()
        }
    }
}

fun readFile(filePath: String): ByteArray? {
    try {
        val file = File(filePath)
        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(
            FileInputStream(file)
        )

        inputBuffer.read(fileContents)
        inputBuffer.close()

        return fileContents
    } catch (e:Exception){
        Log.i("FileUtils", "Problem during reading file: ${e.message}")
        e.printStackTrace()
        return null
    }
}