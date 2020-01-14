package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.FontsContract
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import es.miguelromeral.secretmanager.classes.getRealPathFromURI
import es.miguelromeral.secretmanager.classes.readFile
import es.miguelromeral.secretmanager.classes.readTextFromUri
import es.miguelromeral.secretmanager.classes.writeFile
import es.miguelromeral.secretmanager.ui.createAlertDialog
import es.miguelromeral.secretmanager.ui.models.FileItem
import es.miguelromeral.secretmanager.ui.readableFileSize
import java.io.File
import java.lang.Exception
import java.io.FileInputStream
import java.security.AccessController.getContext


class FileConverterViewModel
    (application: Application) :
    AndroidViewModel(application)
{

    private val _item = FileItem()
    val item: FileItem = _item





    private fun write(context: Context, to: Uri?): Boolean{
        item.output?.let{
            if(writeFile(context, to, it)){
                Log.i(TAG, "File written")
                return true

            }else{
                Toast.makeText(context!!, "There was a problem writing the file. Please try again later", Toast.LENGTH_LONG).show()
                Log.i(TAG, "File not written")
            }
        }
        return false
    }


    fun execute(context: Context, outputFileUri: Uri?){

        if(item.uri == null){
            Toast.makeText(context, "There's no file to process", Toast.LENGTH_LONG).show()
            return
        }

        val stream = readTextFromUri(item.uri!!, context.contentResolver)

        if(stream == null){
            Toast.makeText(context, "The file is empty.", Toast.LENGTH_LONG).show()
            return
        }


        when (item.decrypt) {
            true -> {
                if(item.decrypt(stream)){
                    if(write(context, outputFileUri))
                        Toast.makeText(context, "File decrypted successfully!", Toast.LENGTH_LONG).show()
                }else{
                    val builder = createAlertDialog(context!!,
                        title = "Error while decryption",
                        body = "The password doesn't match with the file to be decrypted",
                        negative = "OK")
                    builder.create().show()
                }
            }
            false -> {
                if(item.encrypt(stream)){
                    if(write(context, outputFileUri))
                        Toast.makeText(context, "File encrypted successfully!", Toast.LENGTH_LONG).show()
                }else{
                    val builder = createAlertDialog(context!!,
                        title = "Error while encryption",
                        body = "There was a problem while encrypting your file. Please, try again later",
                        negative = "OK")
                    builder.create().show()
                }
            }
        }
    }


    fun proccessNewFile(context: Context, data: Uri?){

        item.output = null

        if(data != null){
            val cursor: Cursor? = context.contentResolver.query(data, null, null, null, null, null)

            cursor?.use{
                if(it.moveToFirst()){
                    val displayName: String = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val size = it.getString(it.getColumnIndex(OpenableColumns.SIZE))
                    item.uri = data
                    item.name = displayName
                    item.size = readableFileSize(size.toLong())
                }
            }

        }else{
            item.uri = null
            item.name = "Unknown"

        }
    }

    companion object{
        private const val TAG = "FileConverterViewModel"
    }
}