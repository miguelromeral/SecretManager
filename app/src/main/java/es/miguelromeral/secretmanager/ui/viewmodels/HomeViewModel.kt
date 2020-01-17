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
import es.miguelromeral.secretmanager.ui.models.TextItem
import kotlinx.coroutines.*

class HomeViewModel(
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)




    private val _item = TextItem()
    val item: TextItem = _item

    fun execute(context: Context) {
        when(item.decrypt){
            false -> {
                if(item.encrypt() && item.store){
                    if(item.alias.isNotEmpty()) {
                        uiScope.launch {
                            createNewSecret()
                            Toast.makeText(context, context.getString(R.string.secret_stored, item.alias), Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, R.string.error_unprovided_alias, Toast.LENGTH_LONG).show()
                    }
                }
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