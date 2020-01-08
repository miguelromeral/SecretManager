package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.database.SecretDatabaseDao

class SecretsViewModel (
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}