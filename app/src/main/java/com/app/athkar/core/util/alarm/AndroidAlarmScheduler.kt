package com.app.athkar.core.util.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.app.athkar.edit_prayer.broadcast_receiver.AlarmReceiver

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem, onPermissionDenied: () -> Unit) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    onPermissionDenied()
                    return
                }
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("prayer_name", item.message)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.date.time,
                PendingIntent.getBroadcast(
                    context,
                    item.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } catch (e: Exception) {
            Log.e("AndroidAlarmScheduler", "schedule: ${e.message}")
        }
    }

    override fun cancel(item: AlarmItem) {
        try {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        } catch (e: Exception) {
            Log.e("AndroidAlarmScheduler", "cancel: ${e.message}")
        }
    }
}