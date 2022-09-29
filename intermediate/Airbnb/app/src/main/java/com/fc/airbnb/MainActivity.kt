package com.fc.airbnb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fc.airbnb.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    private var binding: ActivityMainBinding? = null
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // 맵뷰에 생명주기 연결결
       binding?.mapView?.onCreate(savedInstanceState)

        // 맵 가져오기
        // 메인 액티비티에 onMapReadyCallback을 상속시켜 액티비티 자체를 구현체로 만들어 콜백으로 사용한다.
        // 람다로 쓰는 방식도 있긴한데 오버라이드 할것이 많아서 이렇게 사용한다.
        binding?.mapView?.getMapAsync(this)
    }

    // OnMapReadyCallback의 실 구현체 (지도 조작)
    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 최대 최소 줌 정도 설정
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        // 초기 설정 지역 (강남역)
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
        naverMap.moveCamera(cameraUpdate)

        //현 위치 버튼 생성 및 현 위치 이동(권한 필요)
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true

        //location service 등록  (권한 생성)
        locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        // 마커 찍기
        val marker = Marker()
        marker.position = LatLng(37.123123,127.123123)
        marker.map = naverMap
    }

    // 권한 요청 후 실행
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE){
            return
        }
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)){
            if(!locationSource.isActivated){
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    override fun onStart() {
        super.onStart()
        binding?.mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding?.mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding?.mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapView?.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        binding?.mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding?.mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        binding?.mapView?.onDestroy()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}