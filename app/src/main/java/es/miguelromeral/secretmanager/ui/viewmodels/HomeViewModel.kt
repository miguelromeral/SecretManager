package es.miguelromeral.secretmanager.ui.viewmodels

import android.app.Application
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.miguelromeral.secretmanager.ui.models.TextItem

class HomeViewModel
    (application: Application) :
    AndroidViewModel(application)
{

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text



    private val _item = TextItem().apply {

    }
    val item: TextItem = _item


}