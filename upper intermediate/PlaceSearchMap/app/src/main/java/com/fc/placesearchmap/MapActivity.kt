package com.fc.placesearchmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fc.placesearchmap.databinding.ActivityMapBinding
import com.fc.placesearchmap.model.SearchResultEntity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var binding: ActivityMapBinding? = null
    private lateinit var naverMap: NaverMap
    private lateinit var searchResult: SearchResultEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // 인텐트에서 데이터 가져오기
        if(::searchResult.isInitialized.not()) {
            intent?.let{
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCCH_RESULT_EXTRA_KEY) ?: throw Exception("데이터가 존재하진 않습니다.")
                setUpNaverMap(savedInstanceState)
            }
        }
    }



    private fun setUpNaverMap(savedInstanceState: Bundle?) {
        // 맵뷰에 생명주기 연결결
        binding?.map?.onCreate(savedInstanceState)

        // 맵 가져오기
        // 메인 액티비티에 onMapReadyCallback을 상속시켜 액티비티 자체를 구현체로 만들어 콜백으로 사용한다.
        // 람다로 쓰는 방식도 있긴한데 오버라이드 할것이 많아서 이렇게 사용한다.
        binding?.map?.getMapAsync(this)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        setUpMarkerAndInfoWindow(searchResult)
    }


    private fun setUpMarkerAndInfoWindow(searchResultEntity: SearchResultEntity) {
        val lat = searchResultEntity.locationLatLng.latitude.toDouble()
        val lng = searchResultEntity.locationLatLng.longitude.toDouble()

        // 중심지는 검색지역으로 (마커)
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lng))
        naverMap.moveCamera(cameraUpdate)

        // 마커 찍기
        val marker = Marker()

        marker.apply{
            position = LatLng(lat, lng)
            map = naverMap
            title = searchResultEntity.name
        }

        // 정보창 보여주기
        val infoWindow = InfoWindow()
        infoWindow.adapter = object: InfoWindow.DefaultTextAdapter(baseContext){
            override fun getText(p0: InfoWindow): CharSequence {
                return "${searchResult.fullAddress}\n\n${searchResult.name}"
            }
        }
        infoWindow.apply{
            position = LatLng(lat + 0.0018, lng)
            open(naverMap)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
        binding?.map?.onDestroy()
    }
    override fun onStop() {
        super.onStop()
        binding?.map?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding?.map?.onLowMemory()
    }
    companion object {
        const val SEARCCH_RESULT_EXTRA_KEY = "SEARCCH_RESULT_EXTRA_KEY"
    }
}