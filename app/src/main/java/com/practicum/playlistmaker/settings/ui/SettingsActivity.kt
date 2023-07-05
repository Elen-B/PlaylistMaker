package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private val scNightTheme: SwitchCompat by lazy { findViewById(R.id.scNightTheme) }

    private val viewModel: SettingsViewModel by lazy { ViewModelProvider(this, SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btSettingsBack = findViewById<ImageButton>(R.id.btSettingsBack)
        val btShareApp = findViewById<Button>(R.id.btShareApp)
        val btService = findViewById<Button>(R.id.btService)
        val btTermsOfUse = findViewById<Button>(R.id.btTermsOfUse)

        scNightTheme.isChecked = (applicationContext as App).darkTheme

        btSettingsBack.setOnClickListener {
            finish()
        }

        scNightTheme.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).setAppDarkTheme(isChecked)
            (applicationContext as App).saveAppPreferences(App.DARK_THEME_KEY, isChecked)
        }

        btShareApp.setOnClickListener {
            viewModel.onShareAppBtnClick()
        }

        btService.setOnClickListener {
            viewModel.onServiceBtnClick()
        }

        btTermsOfUse.setOnClickListener {
            viewModel.onTermsOfUseBtnClick()
        }
    }
}