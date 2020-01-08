package es.miguelromeral.secretmanager.ui.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import es.miguelromeral.secretmanager.ui.viewmodels.HomeViewModel
import java.lang.IllegalArgumentException

class HomeFactory (
    private val database: SecretDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override  fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}