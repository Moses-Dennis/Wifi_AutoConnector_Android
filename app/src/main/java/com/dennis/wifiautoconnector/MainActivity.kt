package com.dennis.wifiautoconnector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var overallStatus: TextView
    private lateinit var statusDetail: TextView
    private lateinit var wifiValue: TextView
    private lateinit var internetValue: TextView
    private var networkCallback:
        ConnectivityManager.NetworkCallback? = null

    private var currentDefaultNetwork: Network? = null

    private val navy = Color.parseColor("#0D1424")
    private val cardColor = Color.parseColor("#17243A")
    private val blue = Color.parseColor("#397EE8")
    private val lightBlue = Color.parseColor("#7CB7FF")
    private val white = Color.WHITE
    private val muted = Color.parseColor("#A8B5CB")
    private val green = Color.parseColor("#43D6A0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        buildDashboard()
    }

    private fun buildDashboard() {
        val root =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(20), dp(24), dp(20), dp(24))
                setBackgroundColor(navy)
            }

        val scrollView =
            ScrollView(this).apply {
                isFillViewport = true
                addView(
                    root,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    ),
                )
            }
        // Header
        val header =
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

        val logo =
            TextView(this).apply {
                text = "D."
                textSize = 30f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(lightBlue)
                gravity = Gravity.CENTER
                background = rounded(cardColor, 14)
                layoutParams =
                    LinearLayout.LayoutParams(
                        dp(54),
                        dp(54),
                    )
            }

        val heading =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, 0, 0)
            }

        heading.addView(
            makeText("Wi-Fi AutoConnector", 19f, white, true),
        )
        heading.addView(
            makeText("NETWORK DASHBOARD", 11f, muted),
        )

        header.addView(logo)
        header.addView(
            heading,
            LinearLayout.LayoutParams(0, -2, 1f),
        )

        root.addView(header)
        addGap(root, 28)

        // Main status card
        val mainCard = makeCard()

        mainCard.gravity = Gravity.CENTER_HORIZONTAL

        val signalIcon =
            TextView(this).apply {
                text = "⌁"
                textSize = 56f
                setTextColor(lightBlue)
                gravity = Gravity.CENTER
                background =
                    rounded(
                        Color.parseColor("#203D5D"),
                        60,
                    )
                layoutParams =
                    LinearLayout.LayoutParams(
                        dp(104),
                        dp(104),
                    )
            }

        mainCard.addView(signalIcon)
        addGap(mainCard, 18)

        mainCard.addView(
            makeText("NETWORK STATUS", 12f, muted, true),
        )

        overallStatus =
            makeText(
                "Checking connection...",
                23f,
                white,
                true,
            )
        mainCard.addView(overallStatus)

        statusDetail =
            makeText(
                "Waiting for network information",
                14f,
                muted,
            )
        statusDetail.gravity = Gravity.CENTER
        mainCard.addView(statusDetail)

        addGap(root, 20)
        root.addView(
            mainCard,
            LinearLayout.LayoutParams(
                -1,
                -2,
            ),
        )

        addGap(root, 18)

        // Connection details card
        val detailsCard = makeCard()

        detailsCard.addView(
            makeText("CONNECTION DETAILS", 12f, muted, true),
        )

        addGap(detailsCard, 18)

        wifiValue =
            addStatusRow(
                detailsCard,
                "Wi",
                "Wi-Fi connection",
                "Checking...",
            )

        addGap(detailsCard, 18)
        addDivider(detailsCard)
        addGap(detailsCard, 18)

        internetValue =
            addStatusRow(
                detailsCard,
                "↗",
                "Internet access",
                "Checking...",
            )

        root.addView(
            detailsCard,
            LinearLayout.LayoutParams(-1, -2),
        )

        addGap(root, 20)

        // Connect to Wi-Fi button
        val connectButton =
            Button(this).apply {
                text = "Connect to Wi-Fi"
                textSize = 16f
                isAllCaps = false
                setTextColor(white)
                background = rounded(blue, 14)
                setPadding(dp(12), dp(8), dp(12), dp(8))

                setOnClickListener {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                }
            }

        root.addView(
            connectButton,
            LinearLayout.LayoutParams(
                -1,
                dp(56),
            ),
        )

        addGap(root, 12)
        // Manual refresh button
        val checkButton =
            Button(this).apply {
                text = "↻   Check Connection"
                textSize = 16f
                isAllCaps = false
                setTextColor(white)
                background = rounded(blue, 14)
                setPadding(dp(12), dp(8), dp(12), dp(8))
                setOnClickListener {
                    refreshStatus()
                }
            }

        root.addView(
            checkButton,
            LinearLayout.LayoutParams(
                -1,
                dp(56),
            ),
        )

        addGap(root, 20)

        val footer =
            makeText(
                "Made by Dennis  •  Version 1.0",
                12f,
                muted,
            )
        footer.gravity = Gravity.CENTER
        root.addView(footer)

        setContentView(scrollView)
    }

    private fun addStatusRow(
        parent: LinearLayout,
        symbol: String,
        label: String,
        initialValue: String,
    ): TextView {
        val row =
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

        val icon =
            TextView(this).apply {
                text = symbol
                textSize = 19f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(lightBlue)
                gravity = Gravity.CENTER
                background =
                    rounded(
                        Color.parseColor("#203D5D"),
                        12,
                    )
                layoutParams =
                    LinearLayout.LayoutParams(
                        dp(44),
                        dp(44),
                    )
            }

        val labelText = makeText(label, 15f, white, true)

        val valueText =
            makeText(
                initialValue,
                13f,
                muted,
            )
        valueText.gravity = Gravity.END

        val textColumn =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(12), 0, 0, 0)
                addView(labelText)
            }

        row.addView(icon)
        row.addView(
            textColumn,
            LinearLayout.LayoutParams(0, -2, 1f),
        )
        row.addView(valueText)

        parent.addView(
            row,
            LinearLayout.LayoutParams(-1, -2),
        )

        return valueText
    }

    private fun makeCard(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(22))
            background = rounded(cardColor, 22)
        }

    private fun makeText(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false,
    ): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)

            if (bold) {
                setTypeface(typeface, Typeface.BOLD)
            }

            if (value == "Wi-Fi AutoConnector") {
                gravity = Gravity.CENTER_VERTICAL
            }

            setLineSpacing(dp(3).toFloat(), 1f)
        }

    private fun addGap(
        parent: LinearLayout,
        height: Int,
    ) {
        parent.addView(
            View(this),
            LinearLayout.LayoutParams(1, dp(height)),
        )
    }

    private fun addDivider(parent: LinearLayout) {
        parent.addView(
            View(this).apply {
                setBackgroundColor(
                    Color.parseColor("#29354A"),
                )
            },
            LinearLayout.LayoutParams(-1, dp(1)),
        )
    }

    private fun rounded(
        color: Int,
        radius: Int,
    ): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }

    private fun dp(value: Int): Int =
        (
            value * resources.displayMetrics.density
        ).toInt()

    // Network monitoring

    override fun onResume() {
        super.onResume()

        val callback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    currentDefaultNetwork = network

                    runOnUiThread {
                        overallStatus.text =
                            "Checking connection..."
                        statusDetail.text =
                            "Network detected; checking access"
                    }
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: NetworkCapabilities,
                ) {
                    if (currentDefaultNetwork == network) {
                        runOnUiThread {
                            updateStatus(capabilities)
                        }
                    }
                }

                override fun onLost(network: Network) {
                    if (currentDefaultNetwork == network) {
                        currentDefaultNetwork = null

                        runOnUiThread {
                            updateStatus(null)
                        }
                    }
                }
            }

        networkCallback = callback

        connectivityManager.registerDefaultNetworkCallback(
            callback,
        )

        refreshStatus()
    }

    override fun onPause() {
        networkCallback?.let { callback ->
            connectivityManager.unregisterNetworkCallback(
                callback,
            )
        }

        networkCallback = null
        currentDefaultNetwork = null

        super.onPause()
    }

    private fun refreshStatus() {
        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)

        currentDefaultNetwork = network
        updateStatus(capabilities)
    }

    private fun updateStatus(capabilities: NetworkCapabilities?) {
        val wifiConnected =
            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI,
            ) == true

        val internetAvailable =
            capabilities?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED,
            ) == true

        val captivePortalDetected =
            capabilities?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL,
            ) == true

        // Update Wi-Fi status
        wifiValue.text =
            if (wifiConnected) "Connected" else "Not connected"

        wifiValue.setTextColor(
            if (wifiConnected) green else muted,
        )

        // Update internet status
        internetValue.text =
            when {
                internetAvailable -> "Available"
                captivePortalDetected -> "Sign-in required"
                capabilities != null -> "Not verified"
                else -> "Unavailable"
            }

        internetValue.setTextColor(
            when {
                internetAvailable -> green
                captivePortalDetected -> lightBlue
                else -> muted
            },
        )

        // Update the main dashboard message
        when {
            wifiConnected && internetAvailable -> {
                overallStatus.text = "You're connected"
                statusDetail.text =
                    "Wi-Fi and internet are working"
            }

            wifiConnected && captivePortalDetected -> {
                overallStatus.text = "Sign-in required"
                statusDetail.text =
                    "Captive portal detected. Authentication is needed."
            }

            wifiConnected -> {
                overallStatus.text = "Wi-Fi connected"
                statusDetail.text =
                    "Internet access is not verified"
            }

            internetAvailable -> {
                overallStatus.text = "Internet available"
                statusDetail.text =
                    "You're using another network"
            }

            else -> {
                overallStatus.text = "Wi-Fi disconnected"
                statusDetail.text =
                    "No verified internet connection"
            }
        }
    }
}
