package com.fc.usedtrade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fc.usedtrade.chatlist.ChatListFragment
import com.fc.usedtrade.databinding.ActivityMainBinding
import com.fc.usedtrade.home.HomeFragment
import com.fc.usedtrade.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        // 초기화로 홈화면 프래그먼트 보여주기
        replaceFragment(homeFragment)

        binding?.bottomNavigationView?.setOnItemReselectedListener{
            when(it.itemId){
                R.id.home-> replaceFragment(homeFragment)
                R.id.chatList-> replaceFragment(chatListFragment)
                R.id.myPage-> replaceFragment(myPageFragment)
            }
        }
    }

    //프래그먼트 전환
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }
    }
}