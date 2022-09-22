package com.fc.bookreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.bookreview.adapter.BookAdapter
import com.fc.bookreview.api.BookService
import com.fc.bookreview.databinding.ActivityMainBinding
import com.fc.bookreview.model.SearchBookDto
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val adapter = BookAdapter()
    private lateinit var binding: ActivityMainBinding
    private lateinit var bookService: BookService
    private lateinit var keys: JSONObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/ ")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)
        keys = getJsonObject()

        bookService.getBooksByName(keys.getString(CLIENT_ID), keys.getString(CLIENT_SECRET), "책")
            .enqueue(object: Callback<SearchBookDto>{
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    if(response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS")
                        return
                    }
                    response.body()?.let{
                        adapter.submitList(it.books)
                    }
                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }

            })

        binding.searchEditText.setOnKeyListener { view, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == MotionEvent.ACTION_DOWN){
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener  true
            }
            return@setOnKeyListener false
        }
    }

    private fun search(keyWord: String){
        keys = getJsonObject()
        bookService.getBooksByName(keys.getString(CLIENT_ID), keys.getString(CLIENT_SECRET), keyWord)
            .enqueue(object: Callback<SearchBookDto>{
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    if(response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS")
                        return
                    }
                    adapter.submitList(response.body()?.books.orEmpty())

                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }

            })
    }

    private fun initRecyclerView() {
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

    private fun getJsonObject(): JSONObject{
        val jsonString = assets.open(KEY).reader().readText()
        val jsonArray = JSONArray(jsonString)

        return jsonArray.getJSONObject(0)
    }

    companion object{
        private const val KEY = "key.json"
        private const val CLIENT_ID = "client_id"
        private const val CLIENT_SECRET = "client_secret"
        private const val TAG = "MainActivity"
    }
}