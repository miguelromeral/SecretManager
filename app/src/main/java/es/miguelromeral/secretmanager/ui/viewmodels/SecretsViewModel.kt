package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job

class SecretsViewModel (
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val secrets = database.getAllSecrets()



}