package es.miguelromeral.secretmanager.ui.listeners

import es.miguelromeral.secretmanager.database.Secret

class RemoveSecretListener(val clickListener: (item: Secret) -> Unit){
    fun onClick(item: Secret) = clickListener(item)
}