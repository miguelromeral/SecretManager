package es.miguelromeral.secretmanager.ui.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class TextItem : BaseObservable() {


    @Bindable
    var input: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.input)
        }
        get() = field

}
