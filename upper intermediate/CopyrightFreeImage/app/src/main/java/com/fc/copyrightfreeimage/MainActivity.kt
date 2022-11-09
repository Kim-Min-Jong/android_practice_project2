package com.fc.copyrightfreeimage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fc.copyrightfreeimage.data.Repository
import com.fc.copyrightfreeimage.data.adapter.PhotoAdapter
import com.fc.copyrightfreeimage.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initViews()
        bindViews()
        fetchRandomPhotos()
    }

    private fun initViews() {
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = PhotoAdapter()
        }
    }

    private fun bindViews() {
        binding?.searchEditText?.setOnEditorActionListener { editText, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentFocus?.let {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

                    it.clearFocus()
                }

                fetchRandomPhotos(editText.text.toString())
            }
            true
        }

        binding?.refreshLayout?.setOnRefreshListener {
            fetchRandomPhotos(binding?.searchEditText?.text.toString())
        }
    }

    private fun fetchRandomPhotos(query: String? = null) {
        scope.launch {
            // 렌더링시 에러처리
            try {
                Repository.getRandomPhotos(query)?.let { photos ->

                    binding?.errorDescriptionTextView?.visibility = View.GONE
                    (binding?.recyclerView?.adapter as? PhotoAdapter)?.apply {
                        this.photos = photos
                        notifyDataSetChanged()
                    }
                }
                binding?.recyclerView?.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding?.recyclerView?.visibility = View.INVISIBLE
                binding?.errorDescriptionTextView?.visibility = View.VISIBLE
            } finally {
                binding?.shimmerLayout?.visibility = View.GONE
                binding?.refreshLayout?.isRefreshing = false
            }
        }
    }


override fun onDestroy() {
    super.onDestroy()
    binding = null
    scope.cancel()
}
}