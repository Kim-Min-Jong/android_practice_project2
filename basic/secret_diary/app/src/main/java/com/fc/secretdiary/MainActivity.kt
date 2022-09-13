package com.fc.secretdiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {
    // 뷰의 컴포넌트는 onCreate이후에 초기화 되어야 하므로 lazy하게 선언
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1).apply {
            minValue = 0
            maxValue = 9
        }
    }
    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2).apply {
            minValue = 0
            maxValue = 9
        }
    }
    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3).apply {
            minValue = 0
            maxValue = 9
        }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.open_button)
    }
    private val changePwdButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.change_pwd)
    }

    private var changePwdMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {
            if (changePwdMode) {
                Toast.makeText(this, "비밀번호 변경중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val pwdFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            val pwd = sharedPreference.getString("password", "000")
            if (pwd.equals(pwdFromUser)) {
                // 성공
                startActivity(Intent(this, DiaryActivity::class.java))
            } else {
                showErrPopUp()
            }
        }

        changePwdButton.setOnClickListener {
            val sharedPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val pwdFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            if (changePwdMode) {
                // 번호를 저장하는 기능
                sharedPreference.edit {
                    putString("password", pwdFromUser)
                    commit()
                }
                changePwdMode = false
                changePwdButton.setBackgroundColor(Color.BLACK)
            } else {
                // 번호변경모드가 활성화  || 비밀번호가 맞는지 체크
                val pwd = sharedPreference.getString("password", "000")
                if (pwd.equals(pwdFromUser)) {
                    changePwdMode = true
                    Toast.makeText(this, "변경할 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                    changePwdButton.setBackgroundColor(Color.RED)
                } else {
                    showErrPopUp()
                }
            }
        }

    }

    private fun showErrPopUp() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .create().show()
    }
}