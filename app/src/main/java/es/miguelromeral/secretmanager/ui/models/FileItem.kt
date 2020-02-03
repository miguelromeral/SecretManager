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

private const val TAG = "FileItem"

class FileItem : BaseObservable(), IExecutable {

    override val myCipher = MyCipher()


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
    override var decrypt: Boolean = false
        set(value){
            field = value
            notifyPropertyChanged(BR.decrypt)
        }
        get() = field



    private fun updateReady(){
        ready = (uri != null && password.isNotEmpty())
    }


    @Bindable
    override var ready: Boolean = false
        set(value){
            field = value
            notifyPropertyChanged(BR.ready)
        }
        get() = field

    @Bindable
    override var password: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
            updateReady()
        }
        get() = field


    var input: ByteArray? = null

    var output: ByteArray? = null





    override fun encrypt(): Boolean {
        try{
            input?.let {
                output = myCipher.encrypt(it, password)
                return true
            }
            return false
        }catch (e: Exception){
            return false
        }
    }

    override fun decrypt(): Boolean {
        try{
            input?.let {
                output = myCipher.decrypt(it, password)
                return true
            }
            return false
        }catch (e: Exception){
            return false
        }
    }
}
