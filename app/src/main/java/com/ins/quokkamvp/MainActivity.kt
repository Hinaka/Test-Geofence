package com.ins.quokkamvp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.ins.quokkamvp.logger.LogDao
import com.ins.quokkamvp.logger.LogEntity
import com.ins.quokkamvp.logger.LogScreen
import com.ins.quokkamvp.ui.theme.QuokkaMVPTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var geofencingClient: GeofencingClient

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(this)

        setContent {
            QuokkaMVPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showLogs by remember { mutableStateOf(false) }

                    if (!showLogs) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            val permissions = rememberMultiplePermissionsState(
                                listOf(
                                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.POST_NOTIFICATIONS,
                                )
                            )

                            if (permissions.allPermissionsGranted) {
                                Button(onClick = { startNavigating() }) {
                                    Text(text = "Start")
                                }
                            } else {
                                Button(onClick = { permissions.launchMultiplePermissionRequest() }) {
                                    Text(text = "Grant permissions")
                                }
                            }

                            Button(onClick = { addLog() }) {

                            }

                            Button(onClick = { showLogs = true }) {
                                Text("Show Logs")
                            }
                        }
                    } else {
                        LogScreen(
                            onBack = { showLogs = false }
                        )
                    }
                }
            }
        }
    }

    private fun addLog() {
        lifecycleScope.launch {
            LogDao.getInstance(this@MainActivity).insertAll(
                LogEntity(message = "fake message")
            )
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

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    private fun startNavigating() {
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("Hinaka", "gefences success")
                val lastLocation = Locations.last()
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${lastLocation.latitude},${lastLocation.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this@MainActivity, LocationService::class.java))
                } else {
                    startService(Intent(this@MainActivity, LocationService::class.java))
                }
            }
            addOnFailureListener {
                Log.d("Hinaka", "geofences failure $it")
            }
        }
    }
}
