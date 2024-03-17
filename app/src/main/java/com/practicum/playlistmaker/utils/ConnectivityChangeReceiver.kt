package com.practicum.playlistmaker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ConnectivityChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_CONNECTIVITY_CHANGE) {
            if (context != null && !networkAvailable(context)) {
                Toast.makeText(context, "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"
    }
}