package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    /*private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory((applicationContext as App).appPreferences)
        )[SettingsViewModel::class.java]
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel.observeDarkTheme().observe(this) {
            binding.scNightTheme.isChecked = viewModel.observeDarkTheme().value == true
        }

        binding.btSettingsBack.setOnClickListener {
            finish()
        }

        binding.scNightTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        binding.btShareApp.setOnClickListener {
            viewModel.onShareAppBtnClick()
        }

        binding.btService.setOnClickListener {
            viewModel.onServiceBtnClick()
        }

        binding.btTermsOfUse.setOnClickListener {
            viewModel.onTermsOfUseBtnClick()
        }
    }
}