package es.miguelromeral.secretmanager.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

fun shareContentText(context: Context, text: String){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}