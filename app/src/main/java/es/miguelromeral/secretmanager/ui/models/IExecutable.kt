package es.miguelromeral.secretmanager.ui.models

import es.miguelromeral.secretmanager.classes.MyCipher

interface IExecutable {

    val myCipher: MyCipher

    var password: String
    var decrypt: Boolean
    var ready: Boolean


    fun encrypt(): Boolean
    fun decrypt(): Boolean

}