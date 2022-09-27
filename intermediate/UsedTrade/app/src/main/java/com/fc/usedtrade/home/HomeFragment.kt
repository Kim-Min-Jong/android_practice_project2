package com.fc.usedtrade.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.usedtrade.R
import com.fc.usedtrade.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null
    private lateinit var articleAdapter: ArticleAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        articleAdapter = ArticleAdapter()
        articleAdapter.submitList(
            mutableListOf<ArticleModel>(
                ArticleModel("0","asd",100000000000,"100",""),
                ArticleModel("0","asd",100000000000,"100",""),
                ArticleModel("0","asd",100000000000,"100","")

            )
        )

        binding?.articleRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.articleRecyclerView?.adapter = articleAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}