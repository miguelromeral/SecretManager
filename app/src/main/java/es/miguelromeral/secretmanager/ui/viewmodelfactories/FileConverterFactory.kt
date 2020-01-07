package es.miguelromeral.secretmanager.ui.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.miguelromeral.secretmanager.ui.viewmodels.FileConverterViewModel
import java.lang.IllegalArgumentException

class FileConverterFactory (
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override  fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FileConverterViewModel::class.java)){
            return FileConverterViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}