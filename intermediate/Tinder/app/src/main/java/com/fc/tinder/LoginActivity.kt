package com.fc.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.fc.tinder.databinding.ActivityLoginBinding
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private var binding: ActivityLoginBinding? = null
    private var backPressedTime: Long = 0
//    private var result = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()){ result ->
//            if(result.resultCode == RESULT_OK){
//                    callbackManager.
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()
        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }

    private fun initLoginButton() {
        binding?.loginButton?.setOnClickListener {
            val email = getInputEmail()
            val pwd = getInputPwd()

            auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) {
                    if(it.isSuccessful){
                        handleSuccessLogin()
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
    private fun initFacebookLoginButton(){
        binding?.facebookLoginButton?.setPermissions("email", "public_profile")
        binding?.facebookLoginButton?.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                // 성공 시 콜백
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) {
                        if(it.isSuccessful){
                            handleSuccessLogin()
                        } else{
                            Toast.makeText(this@LoginActivity,"페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {
                // 로그인 하다가 취소했을 때 실행되는 콜백
            }

            override fun onError(error: FacebookException) {
                //에러시 콜백
                Toast.makeText(this@LoginActivity,"페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun handleSuccessLogin(){
        if(auth.currentUser == null){
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = auth.currentUser?.uid.orEmpty()

        // realtime db 사용  회원 정보 저장  json 형식으로 저장됨  json 트리? 형식이로 밑에 밑에 식으로 추가
        // db안에 Users 안에 userId안에 userId로 저장됨
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        currentUserDB.updateChildren(user)

        this.finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else if (System.currentTimeMillis() <= backPressedTime + 2000) {
            exitProcess(0)
        }
    }
}