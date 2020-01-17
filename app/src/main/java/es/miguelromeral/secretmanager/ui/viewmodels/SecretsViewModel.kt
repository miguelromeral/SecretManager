package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import es.miguelromeral.secretmanager.ui.fragments.SecretsFragmentDirections
import kotlinx.coroutines.*

class SecretsViewModel (
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val secrets = database.getAllSecrets()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val filteredList: MutableLiveData<List<Secret>> = MutableLiveData()



    fun filterSecrets(criteria: String?): Boolean {
        if(criteria == null || criteria.isEmpty()){
            filteredList.postValue(secrets.value)
        }else {
            val tmp = mutableListOf<Secret>()
            val input = criteria.toLowerCase()

            secrets.value?.let {
                for (s in it) {
                    if (s.alias.toLowerCase().contains(criteria)){
                        tmp.add(s)
                    }
                }
            }
            filteredList.postValue(tmp)
        }
        return true
    }


    fun openSecret(context: Context, view: View, item: Secret) {
        view.findNavController().navigate(
            SecretsFragmentDirections.actionNavigationNotificationsToNavigationHome(item.content))
    }



    fun removeSecret(item: Secret) {
        uiScope.launch {
            removeSecretIO(item.id)
        }
    }


    fun clearDatabase() {
        uiScope.launch {
            clearDatabaseIO()
        }
    }

    private suspend fun removeSecretIO(id: Long){
        return withContext(Dispatchers.IO){
            database.deleteFromKey(id)
        }
    }

    private suspend fun clearDatabaseIO(){
        return withContext(Dispatchers.IO){
            database.clearStarts()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}