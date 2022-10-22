package com.fc.placesearchmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.fc.placesearchmap.adapter.SearchRecyclerAdapter
import com.fc.placesearchmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private lateinit var adapter: SearchRecyclerAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initAdapter()
        initViews()
        initData()
        setData()
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData() {

    }

    private fun initViews() = with(binding){
        this?.let{
            emptyResultTextView.isVisible = false
            recyclerView.adapter = adapter
        }

    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter {
            // todo 클릭 리스너
            Toast.makeText(this, "123",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}