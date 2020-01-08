package es.miguelromeral.secretmanager.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dateFormatted")
fun TextView.setDateFormatted(date: Long?) {
    date?.let {
        text = convertLongToTime(date)
    }
}


fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy\\/MM\\/dd HH:mm")
    return format.format(date)
}