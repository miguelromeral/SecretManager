package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.importSecretsFU
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import es.miguelromeral.secretmanager.ui.fragments.SecretsFragmentDirections
import es.miguelromeral.secretmanager.ui.utils.convertLongToTime
import kotlinx.coroutines.*

class SecretsViewModel (
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    lateinit var secrets: LiveData<List<Secret>>

    private var _dataChanged = MutableLiveData<Boolean>()
    val dataChanged: LiveData<Boolean>
        get() = _dataChanged



    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val filteredList: MutableLiveData<List<Secret>> = MutableLiveData()


    init {
        updateAllSecrets()
    }

    fun updateAllSecrets(){
        secrets = database.getAllSecrets()
        _dataChanged.postValue(true)
    }

    fun finalDataChanged(){
        _dataChanged.postValue(false)
    }


    fun filterSecrets(format: String, criteria: String?): Boolean {
        if(criteria == null || criteria.isEmpty()){
            filteredList.postValue(secrets.value)
        }else {
            val tmp = mutableListOf<Secret>()
            val input = criteria.toLowerCase()

            secrets.value?.let {
                for (s in it) {
                    if (s.alias.toLowerCase().contains(criteria) || convertLongToTime(format, s.time).contains(criteria)){
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

    fun importSecrets(list: RecyclerView) = uiScope.launch {

        val task = async(Dispatchers.IO) {
            importSecretsFU(list.context, list, database)

        }

        when(task.await()){
            true -> {
               /* list.adapter?.let {
                    it.notifyDataSetChanged()
                    //secrets = database.getAllSecrets()
                    //filterSecrets(null)
                }*/

                updateAllSecrets()
                //secrets.value = database.getAllSecrets().value

                Log.i("FileUtils", "Everything OK!")
            }
            false -> {
                Log.i("FileUtils", "Something went wrong")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}