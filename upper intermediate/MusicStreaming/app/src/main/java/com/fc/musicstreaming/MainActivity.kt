package com.fc.musicstreaming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fc.musicstreaming.service.webCrawling
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment.newInstance())
            .commit()

        Thread {
            val list = webCrawling()
            Log.e("MainActivity", list.toString())
        }.start()

    }
}