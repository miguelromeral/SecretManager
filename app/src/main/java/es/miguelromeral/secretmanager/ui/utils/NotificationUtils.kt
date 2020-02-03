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
import androidx.core.content.FileProvider
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.actionViewFile
import es.miguelromeral.secretmanager.classes.getFileMimeType
import es.miguelromeral.secretmanager.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

private val NOTIFICATION_ID = 0


fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, file: Uri){

    var myIntent = Intent(Intent.ACTION_VIEW)
    myIntent.data = file
    myIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK

    val contentPendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_files_id)
    )
        .setSmallIcon(R.drawable.sm_logo_72)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setContentTitle(applicationContext.getString(R.string.channel_files_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}