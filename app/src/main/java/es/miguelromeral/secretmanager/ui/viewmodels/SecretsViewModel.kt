package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import es.miguelromeral.secretmanager.ui.fragments.HomeFragment
import es.miguelromeral.secretmanager.ui.fragments.SecretsFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers.id

class SecretsViewModel (
    val database: SecretDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val secrets = database.getAllSecrets()


    fun openSecret(context: Context, view: View, item: Secret) {
        //Toast.makeText(context, "Hola! ${item.id}", Toast.LENGTH_LONG).show()

        view.findNavController().navigate(
            SecretsFragmentDirections.actionNavigationNotificationsToNavigationHome(item.content))
    }

}