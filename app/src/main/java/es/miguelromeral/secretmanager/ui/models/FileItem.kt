package es.miguelromeral.secretmanager.ui.models

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.decode
import es.miguelromeral.secretmanager.classes.encode
import es.miguelromeral.secretmanager.classes.readTextFromUri

private const val TAG = "FileItem"

class FileItem : BaseObservable() {

    val myCipher = MyCipher()


    var uri: Uri? = null
        set(value){
            field = value
            updateReady()
        }
        get() = field


    @Bindable
    var name: String = String()
        set(value){
            field = value
            notifyPropertyChanged(BR.name)
        }
        get() = field

    @Bindable
    var size: String = String()
        set(value){
            field = value
            notifyPropertyChanged(BR.size)
        }
        get() = field


    @Bindable
    var decrypt: Boolean = false
        set(value){
            field = value
            notifyPropertyChanged(BR.decrypt)
        }
        get() = field



    private fun updateReady(){
        ready = (uri != null && password.isNotEmpty())
    }

    @Bindable
    var ready: Boolean = false
        set(value){
            field = value
            notifyPropertyChanged(BR.ready)
        }
        get() = field

    @Bindable
    var password: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
            updateReady()
        }
        get() = field


    var output: ByteArray? = null





    fun encrypt(content: ByteArray): Boolean {
        try{
            output = myCipher.encrypt(content!!, password)
            return true
        }catch (e: Exception){
            return false
        }
    }

    fun decrypt(content: ByteArray): Boolean {
        try{
            output = myCipher.decrypt(content!!, password)
            return true
        }catch (e: Exception){
            return false
        }
    }
}
