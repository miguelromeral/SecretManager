package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.MyQR
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
import es.miguelromeral.secretmanager.network.getSizeQuery
import okhttp3.ResponseBody


class HomeViewModel(
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val tools = MyQR()

    private var _qr = MutableLiveData<ByteArray?>()
    val qr: LiveData<ByteArray?>
        get() = _qr


    private val _item = TextItem()
    val item: TextItem = _item


    private fun generateQR(context: Context, text: String?){

        if(text != null) {
            ApiQR.retrofitService.getImage(
                data = text,
                size = getSizeQuery(PreferenceManager.getDefaultSharedPreferences(context).getString(
                    context.getString(R.string.preference_qr_size_id),
                    context.getString(R.string.qr_size_300)
                ))
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

    fun showQrImage(context: Context, image: ImageView){
        qr.value?.let {
            // We save the image to internal storage according to the preferences.
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                    context.getString(
                        R.string.preference_save_qr_id
                    ), false
                )
            ) {
                tools.createQrImage(context!!, it, item.alias)
            }

            // Set the QR image from raw data
            image.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))

            // Long click behaviour
            image.setOnLongClickListener {view ->
                MyQR.openQRIntent(view.context, item.output)
                return@setOnLongClickListener true
            }
        }
    }

    fun execute(context: Context): Boolean {
        when(item.decrypt){
            false -> {
                if(item.encrypt()){
                    if(item.store) {
                        if (item.alias.isNotEmpty()) {
                            uiScope.launch {
                                createNewSecret()
                                Toast.makeText(
                                    context,
                                    context.getString(
                                        es.miguelromeral.secretmanager.R.string.secret_stored,
                                        item.alias
                                    ),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                es.miguelromeral.secretmanager.R.string.error_unprovided_alias,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    generateQR(context, item.output)
                    return true
                }else{
                    return false
                }
            }
            true -> {
                if(item.decrypt()) {
                    generateQR(context, item.output)
                    return true
                }else
                    return false
            }
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