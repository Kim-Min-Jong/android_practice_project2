package com.fc.pushalarmreciever

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging

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

        initFirebase()
        updateResult()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        updateResult(true)
    }

    private fun initFirebase(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener{
            if(it.isSuccessful){
                tokenTv.text = it.result
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false){
        resultTv.text = (intent.getStringExtra("notificationType") ?: "앱 런쳐") + if(isNewIntent){
            "(으)로 갱신했습니다."
        } else{
            "(으)로 실행했습니다"
        }
    }
}