package es.miguelromeral.secretmanager.ui.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.decode
import es.miguelromeral.secretmanager.classes.encode

class TextItem : BaseObservable() {

    val myCipher = MyCipher()

    @Bindable
    var decrypt: Boolean = false
        set(value){
            field = value
            notifyPropertyChanged(BR.decrypt)
        }
        get() = field

    @Bindable
    var password: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }
        get() = field

    @Bindable
    var input: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.input)
        }
        get() = field

    @Bindable
    var output: String = String()
        set(value){
            field = value
            notifyPropertyChanged(BR.output)
        }
        get() = field


    fun encrypt(): Boolean {
        try{
            output = encode(myCipher.encrypt(input, password))
            return true
        }catch (e: Exception){
            return false
        }
    }
    fun decrypt(): Boolean {
        try{
            output = myCipher.decrypt(decode(input), password)
            return true
        }catch (e: Exception){
            output = ""
            return false
        }
    }
}
