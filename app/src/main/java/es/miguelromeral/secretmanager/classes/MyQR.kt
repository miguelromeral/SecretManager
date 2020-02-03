package es.miguelromeral.secretmanager.classes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.preference.PreferenceManager
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.network.ServiceQR
import java.io.File
import java.io.FileOutputStream

class MyQR {

    fun createQrImage(context: Context, content: ByteArray, name: String = "secret_manager_qr"): Boolean{
        var fos: FileOutputStream? = null
        try {
            val exportDir = File(context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.getAbsolutePath(), "")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val photo = File(exportDir, "${name}.png")

            if (photo.exists()) {
                photo.delete()
            }

            val fos = FileOutputStream(photo.getPath())
            fos.write(content)

            return true
        }catch (e: java.lang.Exception){
            Log.i("TAG", "Good!")
            fos?.close()
        }
        return false
    }

    companion object {
        fun openQRIntent(context: Context, text: String) {
            val size = PreferenceManager.getDefaultSharedPreferences(context).getString(
                    context.getString(R.string.preference_qr_size_id),
                    context.getString(R.string.qr_size_300)
            )

            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(ServiceQR.getURL(text, size!!))
            })
        }
    }
}