package es.miguelromeral.secretmanager.ui.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.getFileMimeType
import es.miguelromeral.secretmanager.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

private val NOTIFICATION_ID = 0


fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, file: Uri){

    /*
    val pathUri = Uri.parse(file.path)
    val contentIntent = Intent(applicationContext, MainActivity::class.java).apply {
        setAction(Intent.ACTION_GET_CONTENT)
        setDataAndType(pathUri, "file/*")
    }
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)
*/
     */


    val intent = Intent(Intent.ACTION_VIEW).apply {
        //setDataAndType(file, getFileMimeType(context!!, file))
        setData(file)
        //setDataAndType(file, "*/*")
    }

/*    val taskStackBuilder = TaskStackBuilder.create(applicationContext).apply {
        addNextIntent(intent)
    }
    val contentPendingIntent = taskStackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
*/
    val contentPendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_files_id)
    )
        .setSmallIcon(R.drawable.sm_logo_72)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentTitle(applicationContext.getString(R.string.channel_files_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}