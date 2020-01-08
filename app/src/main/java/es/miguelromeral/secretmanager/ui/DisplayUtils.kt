package es.miguelromeral.secretmanager.ui

import android.content.Context
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.content.ContextCompat.startActivity
import es.miguelromeral.secretmanager.R
import java.text.DecimalFormat

fun shareContentText(context: Context, text: String){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}


fun showHidePassword(context: Context, cb: CheckBox, etPassword: EditText){
    when(cb.isChecked){
        true -> {
            cb.text = context.getString(R.string.hide_pwd)
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        false -> {
            cb.text = context.getString(R.string.show_pwd)
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}


// https://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
fun readableFileSize(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        size / Math.pow(
            1024.0,
            digitGroups.toDouble()
        )
    ) + " " + units[digitGroups]
}