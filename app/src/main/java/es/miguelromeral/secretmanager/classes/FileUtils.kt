package es.miguelromeral.secretmanager.classes

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Environment.getExternalStorageDirectory
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.sqlite.db.SimpleSQLiteQuery
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import java.io.*
import java.nio.file.Files.exists


private const val FILENAME = "secret_manager.csv"


fun exportSecrets(context: Context, db: SecretDatabase): Uri?{

    val exportDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.getAbsolutePath(), "")
    if (!exportDir.exists()) {
        exportDir.mkdirs()
    }

    val file = File(exportDir, FILENAME)
    try {
        file.createNewFile()
        val csvWrite = CSVWriter(FileWriter(file))
        val curCSV = db.query("SELECT * FROM ${Secret.TABLE_NAME}", null)
        csvWrite.writeNext(curCSV.getColumnNames())
        while (curCSV.moveToNext()) {
            //Which column you want to exprort
            val columnCounts = curCSV.columnCount
            val arrStr  = Array<String>(columnCounts){String()}
            for (i in 0 until columnCounts)
                arrStr[i] = curCSV.getString(i)
            csvWrite.writeNext(arrStr)
            //csvWrite.writeNext(null)
        }
        csvWrite.close()
        curCSV.close()
        //Toast.makeText(context, "Exported", Toast.LENGTH_SHORT).show()
        return Uri.parse(file.absolutePath)
    } catch (sqlEx: Exception) {
        Log.e("ExportCSV", sqlEx.message, sqlEx)
    }
    return null
}


fun importSecrets(db: SecretDatabase){
    val csvReader = CSVReader(FileReader(Environment.DIRECTORY_DOCUMENTS!! + "/" + FILENAME))
    var nextLine: Array<String>? = arrayOf<String>()
    var count = 0
    val columns = StringBuilder()
    val value = StringBuilder()


    while (true) {

        nextLine = csvReader.readNext()
        if(nextLine == null){
            break
        }

        var i = 0
        while(i < nextLine.size){
            if (count == 0) {
                if (i == nextLine.size - 2)
                    columns.append(nextLine[i])
                else
                    columns.append(nextLine[i]).append(",")
            } else {
                if (i == nextLine.size - 2)
                    value.append("'").append(nextLine[i]).append("'")
                else
                    value.append("'").append(nextLine[i]).append("',")
            }
            i++

        }




        Log.d("ImportCSV", "$columns -------" + value)
    }
}



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


fun getFileMimeType(context: Context, data: Uri): String? {
    val cR = context.getContentResolver()
    //val mime = MimeTypeMap.getSingleton()
    return cR.getType(data)
}

// https://developer.android.com/guide/topics/providers/document-provider#c%C3%B3mo-obtener-un-inputstream

@Throws(IOException::class)
fun readTextFromUri(uri: Uri, contentResolver: ContentResolver): ByteArray? {
    val ist = contentResolver.openInputStream(uri)?.readBytes()
    return ist
    /*
    val stringBuilder = StringBuilder()
    contentResolver.openInputStream(uri)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
        }
    }
    return stringBuilder.toString()*/
}


/*

// https://stackoverflow.com/questions/36329379/file-not-found-exception-path-from-uri

/**
 * Get a file path from a Uri. This will get the the path for Storage Access
 * Framework Documents, as well as the _data field for the MediaStore and
 * other file-based ContentProviders.
 *
 * @param context The context.
 * @param uri The Uri to query.
 * @author paulburke
 */
fun getPath(context: Context, uri: Uri): String? {

    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return "${Environment.getExternalStorageDirectory()}/${split[1]}"
            }

            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(uri)) {

            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )

            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return getDataColumn(context, contentUri, selection, selectionArgs)
        }// MediaProvider
        // DownloadsProvider
    } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
        return uri.path
    }// File
    // MediaStore (and general)

    return null
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context The context.
 * @param uri The Uri to query.
 * @param selection (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */


fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {

    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}


/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

*/