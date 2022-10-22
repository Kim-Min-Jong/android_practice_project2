package com.fc.placesearchmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.fc.placesearchmap.adapter.SearchRecyclerAdapter
import com.fc.placesearchmap.databinding.ActivityMainBinding
import com.fc.placesearchmap.model.LocationLatLngEntity
import com.fc.placesearchmap.model.SearchResultEntity
import com.fc.placesearchmap.response.search.Poi
import com.fc.placesearchmap.response.search.Pois
import com.fc.placesearchmap.util.RetrofitUtil
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private var binding: ActivityMainBinding? = null
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois) {
        // 임시 데이터
        val dataList = pois.poi.map{
            SearchResultEntity(
                it.name ?: "빌딩명 없음",
                makeMainAdress(it),
                LocationLatLngEntity(it.noorLat, it.noorLon)
            )
        }

        // submitList
        adapter.setSearchResultList(dataList) {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() = with(binding){
        this?.let{
            emptyResultTextView.isVisible = false
            recyclerView.adapter = adapter
        }

    }

    private fun bindViews() = with(binding){
        this?.let{
            searchButton.setOnClickListener{
                // 검색
                searchKeyword(searchBarInputView.text.toString())
            }
        }
    }
    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun searchKeyword(keyWord: String) {
        // 메인컨텍스트에서 시작
        launch(coroutineContext){
            try{
                // IO 컨택스트 전환
                withContext(Dispatchers.IO){
                    val response = RetrofitUtil.apiService.getSearchLocation(keyword = keyWord)
                    if(response.isSuccessful) {
                        val body = response.body()
                        // 데이터 받기 성공시 다시 메인 컨텍스트 전환후 동작
                        withContext(Dispatchers.Main){
                            Log.e("Response", body.toString())
                            body?.let{ searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "검색하는 과정에서 에러가 발생했습니다. : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
        job.cancel()
    }
}