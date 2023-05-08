package com.ins.quokkamvp.ui.theme

import android.Manifest
import android.os.Build
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
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
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
        Button(onClick = onShowLogsClick) {
            Text(text = "Show Logs")
        }

        Button(onClick = { /*TODO*/ }) {
            Text(text = "Start Navigation")
        }

        Button(onClick = { /*TODO*/ }) {
           Text(text = "Start location service")
        }

        Button(onClick = { /*TODO*/ }) {
           Text(text = "Stop location service")
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
                    Button(onClick = { addGeofences() }) {
                        Text(text = "Add Geofences")
                    }
                }

            } else {
                Button(onClick = { addGeofences() }) {
                    Text(text = "Add Geofences")
                }
            }
        }
    }
}

private fun addGeofences() {

}
