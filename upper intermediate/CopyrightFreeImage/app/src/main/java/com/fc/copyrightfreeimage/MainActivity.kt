package com.fc.copyrightfreeimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fc.copyrightfreeimage.data.Repository
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
        fetchRandomPhotos()
    }

    private fun fetchRandomPhotos(query: String? = null) {
        scope.launch {
            Repository.getRandomPhotos(query)?.let{ photos ->
                photos
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        scope.cancel()
    }
}