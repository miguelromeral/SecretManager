package es.miguelromeral.secretmanager.ui.utils

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import es.miguelromeral.secretmanager.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dateFormatted")
fun TextView.setDateFormatted(date: Long?) {
    date?.let {
        val format = PreferenceManager.getDefaultSharedPreferences(context).getString(
            context.getString(R.string.preference_date_format_id),
            context.getString(R.string.date_format_one)
        ) ?: context.getString(R.string.date_format_one)
        text = convertLongToTime(format, date)
    }
}


fun convertLongToTime(format: String, time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat( format)
    return format.format(date)
}

@BindingAdapter("characterCount")
fun TextView.setCharacterCount(string: String){
    text = resources.getString(R.string.length_characters, string.count())
}