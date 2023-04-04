package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    private val scNightTheme: SwitchCompat by lazy { findViewById(R.id.scNightTheme) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btSettingsBack = findViewById<ImageButton>(R.id.btSettingsBack)
        val btShareApp = findViewById<Button>(R.id.btShareApp)
        val btService = findViewById<Button>(R.id.btService)
        val btTermsOfUse = findViewById<Button>(R.id.btTermsOfUse)

        btSettingsBack.setOnClickListener {
            finish()
        }

        scNightTheme.setOnClickListener {
            var modeValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            if (scNightTheme.isChecked) {
                modeValue = AppCompatDelegate.MODE_NIGHT_YES
            }
            AppCompatDelegate.setDefaultNightMode(modeValue)
        }

        btShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.tune_share_app)))
        }

        btService.setOnClickListener {
            val serviceIntent = Intent(Intent.ACTION_SENDTO)
            serviceIntent.data = Uri.parse(getString(R.string.mailto))
            serviceIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.service_email)))
            serviceIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.service_email_subject))
            serviceIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.service_email_body))
            startActivity(serviceIntent)
        }

        btTermsOfUse.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_use_url)))
            startActivity(browseIntent)
        }
    }
}