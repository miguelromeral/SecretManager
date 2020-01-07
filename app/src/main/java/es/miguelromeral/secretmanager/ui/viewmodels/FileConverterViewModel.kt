package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import es.miguelromeral.secretmanager.classes.getRealPathFromURI
import es.miguelromeral.secretmanager.classes.readFile
import es.miguelromeral.secretmanager.ui.models.FileItem
import es.miguelromeral.secretmanager.ui.readableFileSize

class FileConverterViewModel
    (application: Application) :
    AndroidViewModel(application)
{


    private val _item = FileItem()
    val item: FileItem = _item

    fun execute() {
        if(item.path.isNotEmpty()) {
            val readFile = readFile(item.path)

            /*
            when (item.decrypt) {
                false -> item.encrypt()
                true -> item.decrypt()
            }*/
        }
    }



    fun proccessNewFile(context: Context, data: Uri?){
        if(data != null){
            val cursor: Cursor? = context.contentResolver.query(data, null, null, null, null, null)

            cursor?.use{
                if(it.moveToFirst()){
                    val displayName: String = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val size = it.getString(it.getColumnIndex(OpenableColumns.SIZE))
                    item.path = data.path!!
                    item.name = displayName
                    item.size = readableFileSize(size.toLong())

                }
            }

        }else{
            item.name = "Unknown"
            item.path = String()

        }
    }



}