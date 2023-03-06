package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btSearch = findViewById<Button>(R.id.btSearch)
        val btMedia = findViewById<Button>(R.id.btMedia)
        val btTune = findViewById<Button>(R.id.btTune)

        val buttonClickListener: View.OnClickListener = object: View.OnClickListener {
            override fun onClick(p0: View?) {
                //Toast.makeText(this@MainActivity, "Запускаем поиск!", Toast.LENGTH_SHORT).show()
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        btSearch.setOnClickListener(buttonClickListener)
        btMedia.setOnClickListener {
            val mediaIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(mediaIntent)
        }
        btTune.setOnClickListener {
            val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}