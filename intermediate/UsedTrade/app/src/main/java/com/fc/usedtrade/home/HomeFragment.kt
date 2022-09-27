package com.fc.usedtrade.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.usedtrade.R
import com.fc.usedtrade.databinding.FragmentHomeBinding
import com.fc.usedtrade.util.DBKey.Companion.DB_ARTICLES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleDB: DatabaseReference
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // 받아온 snapshot객체를 getValue를 통해 ArticleModel 데이터형에 파싱함
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            // null 값을 받아올 수 있기에 처리
            articleModel ?: return

            // null이 아니면 리스트에 넣고 리사이클러뷰 어탭터에 주입
            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        // 프래그먼트 이동 시 뷰만 초기화하고 articleList는 초기화하지 않기 때문에 프래그먼트 이동시 이전 데이터가 남아
        // 이전 데이터가 그대로 보여지게 된다. 그렇기 때문에 list를 초기화하여 이전 데이터가 남아있는 문제를 해결한다.
        articleList.clear()
        articleDB = Firebase.database.reference.child(DB_ARTICLES)

        articleAdapter = ArticleAdapter()


        binding?.articleRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.articleRecyclerView?.adapter = articleAdapter

        articleDB.addChildEventListener(listener)
    }

    // 프래그먼트다 다시 보일 떄 변회된 데이터를 감지해 리사이클러뷰 변화
    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    //프래그먼트가 사라질때 db리스너 제거
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        articleDB.removeEventListener(listener)

    }

}