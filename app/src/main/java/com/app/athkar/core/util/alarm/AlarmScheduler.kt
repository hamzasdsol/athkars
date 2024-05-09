package com.app.athkar.core.util.alarm

interface AlarmScheduler {
    fun schedule(item: AlarmItem, onPermissionDenied: () -> Unit)
    fun cancel(item: AlarmItem)
}