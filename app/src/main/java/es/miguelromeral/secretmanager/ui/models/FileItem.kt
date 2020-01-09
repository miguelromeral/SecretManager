package es.miguelromeral.secretmanager.ui.models

import android.net.Uri
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.decode
import es.miguelromeral.secretmanager.classes.encode

private const val TAG = "FileItem"

class FileItem : BaseObservable() {

    val myCipher = MyCipher()


    var uri: Uri? = null
        set(value){
            field = value
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
    var path: String = String()
        set(value){
            field = value
            notifyPropertyChanged(BR.path)
            ready = value.isNotEmpty()
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
        }
        get() = field





    fun encrypt(): Boolean {
        try{

            return true
        }catch (e: Exception){
            return false
        }
    }
    fun decrypt(): Boolean {
        try{
            return true
        }catch (e: Exception){
            return false
        }
    }
}
