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
import androidx.core.net.toFile
import androidx.sqlite.db.SimpleSQLiteQuery
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import kotlinx.coroutines.*
import java.io.*
import java.nio.file.Files.exists
import org.bouncycastle.util.Strings.toByteArray




private const val FILENAME = "secret_manager.csv"
private const val TAG = "FileUtils"


fun importSecretsFU(context: Context, db: SecretDatabase): Uri? {
    val exportDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.getAbsolutePath(), "")
    if (!exportDir.exists()) {
        exportDir.mkdirs()
    }

    val file = File(exportDir, FILENAME)
    try {

        val csvReader = CSVReader(FileReader(file))

        var count = 0
        var columns = StringBuilder()
        var value = StringBuilder()


        val job = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + job)


        var keep = true

        while(keep){
            var nextLine = csvReader.readNext()
            if(nextLine == null){
                keep = false
                break
            }
            var i = 0
            while(i < nextLine.size){
                if(count == 0){
                    if(i == nextLine.size - 1)
                        columns.append(nextLine[i])
                    else
                        columns.append(nextLine[i]).append(",")
                } else {
                    if(i == nextLine.size - 1)
                        value.append("'").append(nextLine[i]).append("'")
                    else
                        value.append("'").append(nextLine[i]).append("',")
                }

                i += 1
            }

            Log.d(TAG, "${columns}-------${value}")
            if(count != 0){
                val query = SimpleSQLiteQuery("Insert INTO "+ Secret.TABLE_NAME + " (" + columns + ") values(" + value +")")
                Log.i(TAG, "Query: ${query.sql}")
                try {
                    uiScope.launch {
                        val s = insertRow(query, db)
                        Log.i(TAG, "Success: $s")
                    }
                }catch (e: java.lang.Exception){
                    Log.i(TAG, "Excepcion while inserting: ${e.message}")
                    e.printStackTrace()
                }
                value = StringBuilder()
            }

            count += 1
        }

        return Uri.parse(file.absolutePath)

    } catch (sqlEx: Exception) {
        Log.e(TAG, sqlEx.message, sqlEx)
    }
    return null
}

suspend fun insertRow(query: SimpleSQLiteQuery, db: SecretDatabase): Boolean{
    return withContext(Dispatchers.IO){
        try {
            db.secretDatabaseDao.insertDataRawFormat(query)
        }catch (e: java.lang.Exception){
            Log.i(TAG, "Exception while inserting query: ${e.message}")
            false
        }
    }
}





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
        Log.e(TAG, sqlEx.message, sqlEx)
    }
    return null
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


fun writeFile(context: Context, to: Uri?, content: ByteArray): Boolean {
    var fos: FileOutputStream? = null
    var success: Boolean = false
    try{
        if(to == null){
            Log.i(TAG, "Uri can't be null. The file won't be written.")
            return false
        }

        context.contentResolver.openFileDescriptor(to, "rw")?.use {
            // use{} lets the document provider know you're done by automatically closing the stream
            fos = FileOutputStream(it.fileDescriptor)
            fos?.let { fos ->
                fos.write(content)
            }
        }

        success = true
    } catch (e: FileNotFoundException) {
        Log.i(TAG, "Exception catched while writing file: ${e.message}.")
        e.printStackTrace()
    } catch (e: IOException) {
        Log.i(TAG, "Exception catched while writing file: ${e.message}.")
        e.printStackTrace()
    } catch (e: java.lang.Exception) {
        Log.i(TAG, "Exception catched while writing file: ${e.message}.")
        e.printStackTrace()
    } finally {
        Log.i(TAG, "FileOutputStream has been closed.")
        fos?.close()
        return success
    }
}


@Throws(IOException::class)
fun readBytes(uri: Uri, contentResolver: ContentResolver): ByteArray {

    val inputStream = contentResolver.openInputStream(uri)

    // this dynamically extends to take the bytes you read
    val byteBuffer = ByteArrayOutputStream()

    // this is storage overwritten on each iteration with bytes
    val bufferSize = 1024
    val buffer = ByteArray(bufferSize)

    // we need to know how may bytes were read to write them to the byteBuffer
    var len = 0
    while (true) {
        len = inputStream?.read(buffer) ?: -1
        if(len == -1)
            break

        byteBuffer.write(buffer, 0, len)
    }

    // and then we can return your byte array.
    return byteBuffer.toByteArray()
}

// https://developer.android.com/guide/topics/providers/document-provider#c%C3%B3mo-obtener-un-inputstream

fun readTextFromUri(uri: Uri, contentResolver: ContentResolver): ByteArray? {
    try {
        val ist = contentResolver.openInputStream(uri)?.readBytes()
        return ist
    }catch (e: java.lang.Exception){
        Log.i("FileUtils", "Read Text Problem: ${e.message}")
        return null
    }
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


