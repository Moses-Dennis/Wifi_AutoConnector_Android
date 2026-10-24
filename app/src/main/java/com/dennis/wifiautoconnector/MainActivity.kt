
package com.dennis.wifiautoconnector

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "Wi-Fi AutoConnector"
            textSize = 26f
            setTextColor(Color.rgb(25, 70, 130))
            gravity = Gravity.CENTER
        }

        val status = TextView(this).apply {
            text = "App initialized successfully!"
            textSize = 18f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        layout.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val spacing = TextView(this).apply { text = ""; height = 24 }
        layout.addView(spacing)
        layout.addView(status)

        setContentView(layout)
    }
}
