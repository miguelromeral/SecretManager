package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.miguelromeral.secretmanager.classes.readTextFromUri
import es.miguelromeral.secretmanager.classes.writeFile
import es.miguelromeral.secretmanager.ui.models.FileItem
import es.miguelromeral.secretmanager.ui.utils.readableFileSize
import es.miguelromeral.secretmanager.ui.utils.sendNotification
import kotlinx.coroutines.*


class FileConverterViewModel
    (application: Application) :
    AndroidViewModel(application)
{

    private val _item = FileItem()
    val item: FileItem = _item

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    fun clearErrorMessage(){
        _errorMessage.postValue(null)
    }

    private var _errorDecrypting = MutableLiveData<Boolean?>()
    val errorDecrypting: LiveData<Boolean?>
        get() = _errorDecrypting

    fun clearErrorExecuting(){
        _errorDecrypting.postValue(null)
    }


    init{
        _loading.postValue(false)
    }


    private fun write(context: Context, to: Uri?): Boolean{
//        item.input?.let{
        item.output?.let{
            if(writeFile(context, to, it)){
                Log.i(TAG, "File written")
                return true
            }else{
                Log.i(TAG, "File not written")
            }
        }
        return false
    }



    private fun isLoading(loading: Boolean){
        item.ready = !loading
        _loading.postValue(loading)
    }


    fun execute(context: Context, outputFileUri: Uri?) {
        uiScope.launch {
            isLoading(true)

/*

            val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

            notificationManager.sendNotification("File encrypted successfully!", context,
                item.uri!!)
*/
            executeInScope(context, outputFileUri)
            isLoading(false)
        }
    }




    private suspend fun executeInScope(context: Context, outputFileUri: Uri?){
        return withContext(Dispatchers.IO) {
            if (item.uri == null) {
                _errorMessage.postValue("There's no file to process")
                return@withContext
            }

            //item.input = readBytes(item.uri!!, context.contentResolver)
            item.input = readTextFromUri(item.uri!!, context.contentResolver)

            if (item.input == null) {
                _errorMessage.postValue("The file is empty.")
                return@withContext
            }

            val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

            when (item.decrypt) {
                true -> {
                    if (item.decrypt()) {
                        if (write(context, outputFileUri)){
                            notificationManager.sendNotification("File ${item.name} decrypted successfully!", context, outputFileUri!!)
//                            _errorMessage.postValue("File decrypted successfully!")
                        }
                    } else {
                        _errorDecrypting.postValue(true)
                    }
                }
                false -> {
                    if (item.encrypt()) {
                        if (write(context, outputFileUri)) {


                            notificationManager.sendNotification("File encrypted successfully!", context,
                                outputFileUri!!)

                            //_errorMessage.postValue("File encrypted successfully!")
                        }
                    } else {
                        _errorDecrypting.postValue(false)
                    }
                }
            }
        }
    }


    fun proccessNewFile(context: Context, data: Uri?){

        item.output = null
        item.input = null

        if(data != null){
            val cursor: Cursor? = context.contentResolver.query(data, null, null, null, null, null)

            cursor?.use{
                if(it.moveToFirst()){
                    val displayName: String = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val size = it.getString(it.getColumnIndex(OpenableColumns.SIZE))
                    item.uri = data
                    item.name = displayName
                    item.size =
                        readableFileSize(size.toLong())
                }
            }

        }else{
            item.uri = null
            item.name = "Unknown"

        }
    }


    override fun onCleared(){
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object{
        private const val TAG = "FileConverterViewModel"
    }
}