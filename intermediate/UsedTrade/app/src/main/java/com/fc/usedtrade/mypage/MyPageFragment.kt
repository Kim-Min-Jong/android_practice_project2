package com.fc.usedtrade.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.fc.usedtrade.R
import com.fc.usedtrade.databinding.FragmentMyPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MyPageFragment : Fragment(R.layout.fragment_my_page) {
    private var binding: FragmentMyPageBinding? = null
    private val auth: FirebaseAuth by lazy{
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyPageBinding.bind(view)

        initEditText()
        initSignInOutBtn()
        initSignUpBtn()

    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser == null) {
            binding?.let { binding ->
                binding.emailEditText.text?.clear()
                binding.passwordEditText.text?.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.isEnabled = true

                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled = false
            }

        } else {
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser?.email)
                binding.passwordEditText.setText("HIDED")
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
                binding.signInOutButton.text = "로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }
        }
    }
    private fun initEditText() {
        binding?.emailEditText?.addTextChangedListener {
            binding?.let{
                val enable = binding?.emailEditText?.text?.isNotEmpty() ?: false &&
                        binding?.passwordEditText?.text?.isNotEmpty() ?: false
                binding?.signInOutButton?.isEnabled = enable
                binding?.signUpButton?.isEnabled = enable
            }
        }
        binding?.passwordEditText?.addTextChangedListener {
            val enable = binding?.emailEditText?.text?.isNotEmpty() ?: false  &&
                    binding?.passwordEditText?.text?.isNotEmpty() ?: false
            binding?.signInOutButton?.isEnabled = enable
            binding?.signUpButton?.isEnabled = enable
        }
    }

    private fun initSignInOutBtn() {
        binding?.signInOutButton?.setOnClickListener {
            binding?.let {
                val email = it.emailEditText.text.toString()
                val password = it.passwordEditText.text.toString()
                if(auth.currentUser == null){
                    // 로그인
                    auth.signInWithEmailAndPassword(email, password)
                        // requireActivity 시 null 값이 올 수 있다. 프래그먼트가 액티비티에 확실하게 있다는게 보장되면 처리를 안해도 되겠지만
                        // 기본적으로 nullable한 것은 null 전처리를 해주는 것이 좋다.
                        .addOnCompleteListener(requireActivity()) {
                            if(it.isSuccessful){
                                successSignIn()
                            } else {
                                Toast.makeText(context, "로그인에 실패했습니다. 계정정보를 확인해주세요", Toast.LENGTH_SHORT).show()
                            }

                        }
                } else{
                    //로그아웃
                    auth.signOut()
                    binding?.emailEditText?.text?.clear()
                    binding?.emailEditText?.isEnabled = true
                    binding?.passwordEditText?.text?.clear()
                    binding?.passwordEditText?.isEnabled = true

                    binding?.signInOutButton?.text = "로그인"
                    binding?.signInOutButton?.isEnabled = false
                    binding?.signUpButton?.isEnabled = false
                }


            }
        }
    }
    private fun initSignUpBtn() {
        binding?.signUpButton?.setOnClickListener {
            binding?.let{
                val email = it.emailEditText.text.toString()
                val password = it.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if(task.isSuccessful){
                            Toast.makeText(context, "회원가입에 실패했습니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(context, "회원가입에 실패했습니다. 이미 가입한 이메일일수 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
    }

    private fun successSignIn() {
        if(auth.currentUser == null) {
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}