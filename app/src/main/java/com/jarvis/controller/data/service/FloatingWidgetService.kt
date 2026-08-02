package com.jarvis.controller.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

class FloatingWidgetService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Floating widget service started")
        return START_STICKY
    }

    override fun onDestroy() {
        Timber.d("Floating widget service destroyed")
        super.onDestroy()
    }
}
