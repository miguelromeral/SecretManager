package es.miguelromeral.secretmanager.ui.listeners

import android.view.View
import es.miguelromeral.secretmanager.database.Secret

class DecryptSecretListener(val clickListener: (item: Secret) -> Unit){
    fun onClick(item: Secret) = clickListener(item)
}