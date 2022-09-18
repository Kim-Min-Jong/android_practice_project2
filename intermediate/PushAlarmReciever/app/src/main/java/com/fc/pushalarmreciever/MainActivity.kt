package com.fc.pushalarmreciever

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val resultTv: TextView by lazy{
        findViewById<TextView>(R.id.resultTv)
    }
    private val tokenTv: TextView by lazy{
        findViewById<TextView>(R.id.tokenTv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}