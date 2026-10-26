
package com.dennis.wifiautoconnector

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Android's network service.
        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        // Build the screen.
        val layout =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(32, 32, 32, 32)
                setBackgroundColor(Color.WHITE)
            }

        val title =
            TextView(this).apply {
                text = "Wi-Fi AutoConnector"
                textSize = 26f
                setTextColor(Color.rgb(25, 70, 130))
                gravity = Gravity.CENTER
            }

        status =
            TextView(this).apply {
                text = "Checking connection..."
                textSize = 18f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.CENTER
            }

        layout.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )

        val spacing =
            TextView(this).apply {
                height = 24
            }

        layout.addView(spacing)
        layout.addView(status)

        setContentView(layout)
    }

    // Android calls this when the screen becomes active.
    override fun onStart() {
        super.onStart()

        // Start listening for changes to the default network.
        connectivityManager.registerDefaultNetworkCallback(
            networkCallback,
        )

        // Display the current status immediately.
        refreshCurrentStatus()
    }

    // Android calls this when the screen is no longer active.
    override fun onStop() {
        connectivityManager.unregisterNetworkCallback(
            networkCallback,
        )

        super.onStop()
    }

    // Check the current network when the screen starts.
    private fun refreshCurrentStatus() {
        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)

        updateStatus(capabilities)
    }

    // Listen for network events reported by Android.
    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    status.text =
                        "Network detected\nChecking connection..."
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                runOnUiThread {
                    updateStatus(networkCapabilities)
                }
            }

            override fun onLost(network: Network) {
                runOnUiThread {
                    status.text = "No active network\nWi-Fi disconnected"
                }
            }
        }

    // Convert network information into readable text.
    private fun updateStatus(capabilities: NetworkCapabilities?) {
        val wifiConnected =
            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI,
            ) == true

        val internetAvailable =
            capabilities?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED,
            ) == true

        status.text =
            when {
                wifiConnected && internetAvailable -> {
                    "Wi-Fi connected\nInternet available"
                }

                wifiConnected -> {
                    "Wi-Fi connected\nInternet not verified"
                }

                else -> {
                    "Wi-Fi not connected"
                }
            }
    }
}
