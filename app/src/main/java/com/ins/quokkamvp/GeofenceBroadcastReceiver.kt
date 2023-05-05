package com.ins.quokkamvp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


private const val TAG = "Hinaka"

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "received")
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)!!
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
            || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                geofenceTransition,
                triggeringGeofences!!
            )

            val requestId = triggeringGeofences.firstOrNull()?.requestId.orEmpty()
            val serviceIntent = Intent(context, AudioService::class.java)
            serviceIntent.putExtra("request_id", requestId)
            context?.startService(serviceIntent)

            // Send notification and log the transition details.
            sendNotification(context!!, geofenceTransitionDetails!!)
        } else {
            // Log the error.
            Log.e(
                TAG, "error"
            )
        }
    }

    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String? {
        val geofenceTransitionString: String = "Enter"

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList = ArrayList<String?>()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)
        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    private fun sendNotification(context: Context, message: String) {
        val channelId = "my_channel_id"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for devices running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Create a notification builder
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Geofence")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        Log.d(TAG, "notification = $message")
        // Show the notification
        notificationManager.notify(0, builder.build())
    }
}
