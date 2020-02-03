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
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment.getExternalStorageDirectory
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.recyclerview.widget.RecyclerView
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


suspend fun importSecretsFU(context: Context, recyclerView: RecyclerView, db: SecretDatabaseDao): Boolean {
    try {

        val file = openFile(context)
        val csvReader = CSVReader(FileReader(file))

        val job = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + job)

        var count = 0
        var columns = StringBuilder()
        var value = StringBuilder()

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
                    val s = db.insertDataRawFormat(query)
                    Log.i(TAG, "Success: $s")
                }catch (e: java.lang.Exception){
                    Log.i(TAG, "Excepcion while inserting: ${e.message}")
                    e.printStackTrace()
                }
                value = StringBuilder()
            }

            count += 1
        }

        return count != 1

    } catch (sqlEx: Exception) {
        Log.e(TAG, sqlEx.message, sqlEx)
    }
    return false
}

private fun openFile(context: Context, name: String? = FILENAME): File{
    val exportDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.getAbsolutePath(), "")
    if (!exportDir.exists()) {
        exportDir.mkdirs()
    }
    return File(exportDir, name)
}

fun exportSecrets(context: Context, db: SecretDatabase): Uri?{
    val file = openFile(context)
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
        }
        csvWrite.close()
        curCSV.close()
        return Uri.parse(file.absolutePath)
    } catch (sqlEx: Exception) {
        Log.e(TAG, sqlEx.message, sqlEx)
    }
    return null
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
    } catch (e: java.lang.Exception) {
        Log.i(TAG, "Exception catched while writing file: ${e.message}.")
    } finally {
        fos?.close()
        Log.i(TAG, "FileOutputStream has been closed.")
        return success
    }
}


fun actionViewFile(context:Context, uri: Uri): Intent {
    var file = File(uri.path)

    var auth = context.getApplicationContext().getPackageName()

    var nu = FileProvider.getUriForFile(context,
        auth + ".provider",
        file)

    var myIntent = Intent(Intent.ACTION_VIEW)
    /*myIntent.setDataAndType(
        nu,
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("csv")
    )*/
    myIntent.data = nu
    myIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    return myIntent
}