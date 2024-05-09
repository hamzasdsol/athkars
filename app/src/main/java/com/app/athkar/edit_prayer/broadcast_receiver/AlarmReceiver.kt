package com.app.athkar.edit_prayer.broadcast_receiver

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.app.athkar.MainActivity
import com.app.athkar.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name")

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        /**
         * Default Notification Sound
         */
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, "prayer_channel")
            .setSmallIcon(R.drawable.ic_isha)
            .setContentTitle("Prayer Reminder")
            .setContentText("$prayerName prayer is almost now")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(getPendingIntent(context))
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notificationManager.getNotificationChannel("prayer_channel") == null
        ) {
            val name = "Prayer Channel"
            val descriptionText = "Channel for prayer notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("prayer_channel", name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.BLUE
                setShowBadge(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val resultIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
