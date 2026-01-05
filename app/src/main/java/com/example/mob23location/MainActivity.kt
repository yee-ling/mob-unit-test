package com.example.mob23location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mob23location.service.LocationService
import com.example.mob23location.ui.navigation.AppNav
import com.example.mob23location.ui.theme.MOBStarterAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(it) {
            Log.d("debugging", "Permission Granted")
        } else {
            Log.d("debugging", "Permission Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MOBStarterAppTheme {
                ComposeApp()
            }
        }
        checkPermissionAndStart()
        requestNotificationPermission()
    }
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if(perms.all { it.value }) {
//            startTracking()
            checkBGPermissionAndStart()
        }
    }

    private val bgPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(it) {
            startTracking()
        }
    }

    private fun checkPermissionAndStart() {
        val perms = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val missing = perms.filter {
            ContextCompat.checkSelfPermission(
                this, it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if(missing.isEmpty()) {
//            startTracking()
            checkBGPermissionAndStart()
        } else {
            locationPermissionLauncher.launch(missing.toTypedArray())
        }
    }

    private fun checkBGPermissionAndStart() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(ContextCompat.checkSelfPermission(
                this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) {
                bgPermissionLauncher.launch(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
        startTracking()
    }

    private fun startTracking() {
        ContextCompat.startForegroundService(
            this,
            Intent(this, LocationService::class.java))
    }

    private fun stopTracking() {
        stopService(Intent(this, LocationService::class.java))
    }

    // request Notification permission
    fun requestNotificationPermission() {
        if(ActivityCompat.checkSelfPermission(
            this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun ComposeApp() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            AppNav()
        }
    }
}