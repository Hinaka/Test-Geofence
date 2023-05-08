package com.ins.quokkamvp

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.ins.quokkamvp.logger.LogScreen

@Composable
fun MainScreen() {
    var showLogs by remember { mutableStateOf(false) }

    when {
        showLogs -> {
            LogScreen(
                onBack = { showLogs = false }
            )
        }

        else -> {
            HomeScreen(
                onShowLogsClick = { showLogs = true }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onShowLogsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        Button(onClick = onShowLogsClick) {
            Text(text = "Show Logs")
        }

        Button(onClick = { startNavigation(context) }) {
            Text(text = "Start Navigation")
        }

        Button(onClick = { startLocationService(context) }) {
            Text(text = "Start location service")
        }

        Button(onClick = { stopLocationService(context) }) {
            Text(text = "Stop location service")
        }

        Button(onClick = { stopAudio(context) }) {
            Text(text = "Stop audio")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission =
                rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            if (!notificationPermission.status.isGranted) {
                Button(onClick = { notificationPermission.launchPermissionRequest() }) {
                    Text(text = "Request notification permission")
                }
            }
        }

        val locationPermissions = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        if (!locationPermissions.allPermissionsGranted) {
            Button(onClick = { locationPermissions.launchMultiplePermissionRequest() }) {
                Text(text = "Request location permission")
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val backgroundLocationPermission =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION)

                if (!backgroundLocationPermission.status.isGranted) {
                    Button(onClick = { backgroundLocationPermission.launchPermissionRequest() }) {
                        Text(text = "Request background location permission")
                    }
                } else {
                    Button(onClick = { addGeofences(context) }) {
                        Text(text = "Add Geofences")
                    }
                }

            } else {
                Button(onClick = { addGeofences(context) }) {
                    Text(text = "Add Geofences")
                }
            }
        }
    }
}

private fun startNavigation(context: Context) {
    val lastLocation = Locations.last()
    val gmmIntentUri =
        Uri.parse("google.navigation:q=${lastLocation.latitude},${lastLocation.longitude}")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    context.startActivity(mapIntent)
}

private fun startLocationService(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(Intent(context, LocationService::class.java))
    } else {
        context.startService(Intent(context, LocationService::class.java))
    }
}

private fun stopLocationService(context: Context) {
    context.stopService(Intent(context, LocationService::class.java))
}

private fun stopAudio(context: Context) {
    context.stopService(Intent(context, AudioService::class.java))
}

@SuppressLint("MissingPermission")
private fun addGeofences(context: Context) {
    val geofencingClient = LocationServices.getGeofencingClient(context)
    geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent(context)).run {
        addOnSuccessListener {
            Toast.makeText(context, "Add geofences success", Toast.LENGTH_SHORT).show()
        }
        addOnFailureListener {
            Toast.makeText(context, "$it", Toast.LENGTH_LONG).show()
        }
    }
}

private fun getGeofencingRequest(): GeofencingRequest {
    return GeofencingRequest.Builder().apply {
        setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        addGeofences(
            Locations.map(::createGeofence)
        )
    }.build()
}

private fun createGeofence(location: Location): Geofence {
    return Geofence.Builder()
        .setRequestId(location.id)
        .setCircularRegion(
            location.latitude,
            location.longitude,
            150f,
        )
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
        .build()
}

private fun geofencePendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
    return PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )
}
