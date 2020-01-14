package es.miguelromeral.secretmanager.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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


fun createAlertDialog(context: Context, title: Int? = null, body: Int? = null, negative: Int? = null): AlertDialog.Builder{
    val resources = context.resources
    return createAlertDialog(context,
        title = if(title != null) resources.getString(title) else null,
        body = if(body != null) resources.getString(body) else null,
        negative = if(negative != null) resources.getString(negative) else null
        )
}

fun createAlertDialog(context: Context, title: String? = null, body: String? = null, negative: String? = null): AlertDialog.Builder {
    val builder = AlertDialog.Builder(context)
    title?.let{
        builder.setTitle(it)
    }
    body?.let{
        builder.setMessage(it)
    }
    negative?.let{
        builder.setNegativeButton(it, null)
    }
/*
    // Set the alert dialog yes button click listener
    builder.setPositiveButton(resources.getString(es.miguelromeral.secretmanager.R.string.clear_secret_yes),
        DialogInterface.OnClickListener { dialog, which ->

            viewModel.removeSecret(item)
        })


 */
    return builder
}