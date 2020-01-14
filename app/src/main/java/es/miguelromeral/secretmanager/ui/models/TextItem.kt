package es.miguelromeral.secretmanager.ui.models

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.decode
import es.miguelromeral.secretmanager.classes.encode

private const val TAG = "TextItem"

class TextItem : BaseObservable(), IExecutable {

    override val myCipher = MyCipher()

    @Bindable
    override var decrypt: Boolean = false
        set(value){
            field = value
            notifyPropertyChanged(BR.decrypt)
        }
        get() = field


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



    @Bindable
    var input: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.input)
            updateReady()
        }
        get() = field

    private fun updateReady(){
        ready = (input.isNotEmpty() && password.isNotEmpty())
    }

    @Bindable
    var output: String = String()
        set(value){
            field = value
            notifyPropertyChanged(BR.output)
        }
        get() = field



    @Bindable
    var store: Boolean = false
        set(value){
            field = value
            notifyPropertyChanged(BR.store)
        }
        get() = field

    @Bindable
    var alias: String = String()
        set(value){
            field = value
            notifyPropertyChanged(BR.alias)
        }
        get() = field



    override fun encrypt(): Boolean {
        try{
            Log.i(TAG, "Encrypting $input with password $password")
            output = encode(myCipher.encrypt(input, password))
            Log.i(TAG, "Resulting $output")
            //Log.i(TAG, "And its decryption is ${myCipher.decrypt(decode(input), password)}")
            return true
        }catch (e: Exception){
            Log.i(TAG, "Something wrong happened during encryption: ${e.message}")
            e.printStackTrace()
            return false
        }
    }

    override fun decrypt(): Boolean {
        try{
            val decoded = decode(input)
            Log.i(TAG, "Decrypting $decoded with password $password")
            output = String(myCipher.decrypt(decoded, password))
            Log.i(TAG, "Resulting $output")
            Log.i(TAG, "And its encryption is ${myCipher.encrypt(output, password)}")
            return true
        }catch (e: Exception){
            output = ""
            Log.i(TAG, "Something wrong happened during decryption: ${e.message}")
            return false
        }
    }
}
