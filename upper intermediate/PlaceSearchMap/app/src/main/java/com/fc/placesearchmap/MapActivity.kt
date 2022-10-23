package com.fc.placesearchmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.fc.placesearchmap.databinding.ActivityMapBinding
import com.fc.placesearchmap.model.LocationLatLngEntity
import com.fc.placesearchmap.model.SearchResultEntity
import com.fc.placesearchmap.util.RetrofitUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MapActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {
    private var binding: ActivityMapBinding? = null
    private lateinit var naverMap: NaverMap
    private lateinit var searchResult: SearchResultEntity
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private val marker = Marker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        job = Job()

        // 인텐트에서 데이터 가져오기
        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCCH_RESULT_EXTRA_KEY)
                    ?: throw Exception("데이터가 존재하진 않습니다.")
                setUpNaverMap(savedInstanceState)
            }
        }
        bindViews()
    }

    private fun bindViews() = with(binding) {
        this?.let {
            currentLocationButton.setOnClickListener {
                getMyLocation()
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


        marker.apply {
            position = LatLng(lat, lng)
            map = naverMap
            title = searchResultEntity.name
        }

        // 정보창 보여주기
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(baseContext) {
            override fun getText(p0: InfoWindow): CharSequence {
                return "${searchResult.fullAddress}\n\n${searchResult.name}"
            }
        }
        infoWindow.apply {
            position = LatLng(lat + 0.0018, lng)
            open(naverMap)
        }
    }

    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        // 내 위치 얻기위한 권한 요청청
        val isGpsGranted = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsGranted) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), PERMISSION_REQUEST_CODE
                )
            } else {
                setMyLocationListener()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime = 1500L
        val minDistance = 100f
        if (::locationListener.isInitialized.not()) {
            locationListener = LocationListener { p0 ->
                val locationLatLngEntity = LocationLatLngEntity(
                    p0.latitude.toFloat(),
                    p0.longitude.toFloat()
                )
                onCurrentLocationChanged(locationLatLngEntity)
            }
        }

        with(locationManager) {
            requestLocationUpdates(
                GPS_PROVIDER,
                minTime, minDistance, locationListener
            )
        }
    }

    private fun onCurrentLocationChanged(locationLatLng: LocationLatLngEntity) {
        val lat = locationLatLng.latitude.toDouble()
        val lng = locationLatLng.longitude.toDouble()

        // 중심지는 검색지역으로 (마커)
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lng))
        naverMap.moveCamera(cameraUpdate)

        loadReverseGeoInformation(locationLatLng)
        removeLocationListener()
    }

    private fun loadReverseGeoInformation(locationEntity: LocationLatLngEntity) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getReverseGeoCode(
                        lat = locationEntity.latitude.toDouble(),
                        lon = locationEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("list", body.toString())
                            body?.let {
                                setUpMarkerAndInfoWindow(
                                    SearchResultEntity(
                                        fullAddress = it.addressInfo.fullAddress ?: "",
                                        name = "내 위치",
                                        locationLatLng = locationEntity
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MapActivity,
                    "검색하는 과정에서 에러가 발생했습니다. : ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::locationListener.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationListener()
            } else {
                Toast.makeText(this, "권한을 받지 못했습니다", Toast.LENGTH_SHORT).show()
            }
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
        const val PERMISSION_REQUEST_CODE = 101
    }

}