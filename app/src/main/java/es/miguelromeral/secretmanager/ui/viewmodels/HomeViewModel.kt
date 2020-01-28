package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import es.miguelromeral.secretmanager.network.ApiQR
import es.miguelromeral.secretmanager.network.ServiceQR
import es.miguelromeral.secretmanager.ui.models.TextItem
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.nio.file.Files.delete
import java.nio.file.Files.exists
import android.os.Environment.getExternalStorageDirectory
import android.os.Environment
import es.miguelromeral.secretmanager.network.getSizeQuery
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream


class HomeViewModel(
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _qr = MutableLiveData<ByteArray?>()
    val qr: LiveData<ByteArray?>
        get() = _qr


    private val _item = TextItem()
    val item: TextItem = _item


    private fun generateQR(text: String?){

        if(text != null) {
            ApiQR.retrofitService.getProperties(
                data = text,
                size = getSizeQuery()
            ).enqueue(
                object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        try {
                            _qr.value = null
                        } catch (e: Exception) {
                            Log.i("TAG", "Error: " + t.message)
                        }
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        try {
                            val res = response.body()
                            Log.i("TAG", "Result: $res")
                            _qr.value = res?.bytes()

                        } catch (e: Exception) {
                            Log.i("TAG", "Good!")
                        }
                    }

                })
        }
        else
        {
            _qr.value = null
        }
    }

    fun execute(context: Context) {
        when(item.decrypt){
            false -> {
                if(item.encrypt() && item.store){
                    if(item.alias.isNotEmpty()) {
                        uiScope.launch {
                            createNewSecret()
                            Toast.makeText(context, context.getString(es.miguelromeral.secretmanager.R.string.secret_stored, item.alias), Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, es.miguelromeral.secretmanager.R.string.error_unprovided_alias, Toast.LENGTH_LONG).show()
                    }
                }
                generateQR(item.output)
            }
            true -> item.decrypt()
        }
    }


    private suspend fun createNewSecret(){
        return withContext(Dispatchers.IO){
            var secret = Secret(content = item.output, alias = item.alias)
            database.insert(secret)
        }
    }

    override fun onCleared(){
        super.onCleared()
        viewModelJob.cancel()
    }
}