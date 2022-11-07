package com.fc.citymicrodust

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.fc.citymicrodust.data.Repository
import com.fc.citymicrodust.data.model.airquality.Grade
import com.fc.citymicrodust.data.model.airquality.MeasuredValue
import com.fc.citymicrodust.data.model.monitoringstation.MonitoringStation
import com.fc.citymicrodust.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initVariables()
        requestLocationPermissions()
    }


    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        cancellationTokenSource = CancellationTokenSource()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val locationPermissionGranted =
            requestCode == REQUEST_ACCESS_LOCATION_PERMISSIONS &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            finish()
        } else {
            // 실제 위치정보로 측정소 위치 가져오기 todo
            fetchAirQualityData()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchAirQualityData() {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener {
            scope.launch {
                val monitoringStation =
                    Repository.getNearbyMonitoringStation(it.latitude, it.longitude)
                val measuredValue =
                    Repository.getLatestAirQualityData(monitoringStation!!.stationName!!)

                displayAirQualityData(monitoringStation, measuredValue!!)
            }
        }
    }

    private fun displayAirQualityData(
        monitoringStation: MonitoringStation,
        measuredValue: MeasuredValue
    ) {
        binding?.measuringStationAddressTextView?.text = monitoringStation.addr
        binding?.measuringStationName?.text = monitoringStation.stationName

        (measuredValue.khaiGrade ?: Grade.UNKNOWN).let {
            binding?.root?.setBackgroundResource(it.colorResId)
            binding?.totalGradeLabelTextView?.text = it.label
            binding?.totalGradeEmojiTextView?.text = it.emoji
        }

        with(measuredValue) {
            binding?.fineDustInformationTextView?.text =
                "미세먼지: $pm10Value ㎍/㎥ ${(pm10Grade ?: Grade.UNKNOWN).emoji}"
            binding?.ultraFineDustInformationTextView?.text =
                "초미세먼지: $pm25Value ㎍/㎥ ${(pm25Grade ?: Grade.UNKNOWN).emoji}"

            binding?.let {
                with(it.so2Item) {
                    labelTextView.text = "아황산가스"
                    gradeTextView.text = (so2Grade ?: Grade.UNKNOWN).toString()
                    valueTextView.text = "$so2Value ppm"
                }
                with(it.coItem) {
                    labelTextView.text = "일산화탄소"
                    gradeTextView.text = (coGrade ?: Grade.UNKNOWN).toString()
                    valueTextView.text = "$coValue ppm"
                }

                with(it.o3Item) {
                    labelTextView.text = "오존"
                    gradeTextView.text = (o3Grade ?: Grade.UNKNOWN).toString()
                    valueTextView.text = "$o3Value ppm"
                }

                with(it.no2Item) {
                    labelTextView.text = "이산화질소"
                    gradeTextView.text = (no2Grade ?: Grade.UNKNOWN).toString()
                    valueTextView.text = "$no2Value ppm"
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
        cancellationTokenSource?.cancel()
        scope.cancel()
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
    }
}