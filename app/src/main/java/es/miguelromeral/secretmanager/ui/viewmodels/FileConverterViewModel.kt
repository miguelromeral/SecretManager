package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.FileConverter
import es.miguelromeral.secretmanager.classes.writeFile
import es.miguelromeral.secretmanager.ui.models.FileItem
import es.miguelromeral.secretmanager.ui.utils.createAlertDialog
import es.miguelromeral.secretmanager.ui.utils.readableFileSize
import es.miguelromeral.secretmanager.ui.utils.sendNotification
import kotlinx.coroutines.*


class FileConverterViewModel
    (application: Application) :
    AndroidViewModel(application)
{

    private val tools = FileConverter()

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
        item.output?.let{
            if(writeFile(context, to, it)){
                Log.i(TAG, "File has been written.")
                return true
            }else{
                Log.i(TAG, "File hasn't been written.")
            }
        }
        return false
    }

    private fun isLoading(loading: Boolean){
        item.ready = !loading
        _loading.postValue(loading)
    }


    fun execute(context: Context, outputFileUri: Uri?) {
        isLoading(true)
        uiScope.launch {
            executeInScope(context, outputFileUri)
            isLoading(false)
        }
    }


    private suspend fun executeInScope(context: Context, outputFileUri: Uri?){
        return withContext(Dispatchers.IO) {
            if (item.uri == null) {
                _errorMessage.postValue(context.getString(R.string.error_no_file_process))
                return@withContext
            }

            item.input = tools.readTextFromUri(item.uri!!, context)

            if (item.input == null) {
                _errorMessage.postValue(context.getString(R.string.error_empty_file))
                return@withContext
            }

            val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

            when (item.decrypt) {
                true -> {
                    if (item.decrypt()) {
                        if (write(context, outputFileUri)){
                            notificationManager.sendNotification(context.getString(R.string.channel_files_body_decrypted, item.name), context, outputFileUri!!)
                        }
                    } else {
                        _errorDecrypting.postValue(true)
                    }
                }
                false -> {
                    if (item.encrypt()) {
                        if (write(context, outputFileUri)) {
                            notificationManager.sendNotification(context.getString(R.string.channel_files_body_encrypted), context, outputFileUri!!)
                        }
                    } else {
                        _errorDecrypting.postValue(true)
                    }
                }
            }
        }
    }

    private fun clearFileSelected(){
        item.output = null
        item.input = null
        item.uri = null
        item.name = String()
    }

    fun proccessNewFile(context: Context, data: Uri?){

        clearFileSelected()

        if(data != null){
            val cursor: Cursor? = context.contentResolver.query(data, null, null, null, null, null)

            cursor?.use{
                if(it.moveToFirst()){
                    val displayName: String = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val size = it.getString(it.getColumnIndex(OpenableColumns.SIZE))
                    item.uri = data
                    item.name = displayName
                    val size_val = size.toLong()
                    item.size = readableFileSize(size_val)

                    if(size_val > MAX_FILE_SIZE){
                        item.size = context.getString(R.string.error_file_size_not_allowed,
                            readableFileSize(size_val),
                            readableFileSize(size_val - MAX_FILE_SIZE))
                        val bu = createAlertDialog(
                            context,
                            title = context.getString(R.string.error_max_file_size_title),
                            body = context.getString(R.string.error_max_file_size, readableFileSize(MAX_FILE_SIZE.toLong()))
                        )
                        bu.setPositiveButton(context.getString(R.string.ok_answer))
                        {dialog, which ->}

                        val dialog = bu.create()
                        dialog.show()
                        clearFileSelected()
                    }
                }
            }

        }else{
            item.uri = null
            item.name = context.getString(R.string.error_unknown_file)

        }
    }

    override fun onCleared(){
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object{
        private const val TAG = "FileConverterViewModel"

        const val MAX_FILE_SIZE = 50 * 1024 * 1024
    }
}