package com.fc.tinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.fc.tinder.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var binding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
    }

    private fun initLoginButton() {
        binding?.loginButton?.setOnClickListener {
            val email = getInputEmail()
            val pwd = getInputPwd()

            auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) {
                    if(it.isSuccessful){
                        finish()
                    } else{
                        Toast.makeText(this, "로그인에 실패했습니다. 정보를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun initSignUpButton() {
        binding?.signUpButton?.setOnClickListener {
            val email = getInputEmail()
            val pwd = getInputPwd()

            auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) {
                    if(it.isSuccessful){
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인해주세요", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this, "이미 가입한 이메일이거나 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()

                    }
                }
        }
    }
    private fun getInputEmail():String =
        binding?.emailEditText?.text.toString()

    private fun getInputPwd(): String =
        binding?.passwordEditText?.text.toString()

    private fun initEmailAndPasswordEditText() {
        val emailEditText = binding?.emailEditText
        val pwdEditText = binding?.passwordEditText

        emailEditText?.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && pwdEditText?.text!!.isNotEmpty()
            binding?.loginButton?.isEnabled = enable
            binding?.signUpButton?.isEnabled = enable
       }
        pwdEditText?.addTextChangedListener {
            val enable = emailEditText?.text!!.isNotEmpty() && pwdEditText.text.isNotEmpty()
            binding?.loginButton?.isEnabled = enable
            binding?.signUpButton?.isEnabled = enable
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}