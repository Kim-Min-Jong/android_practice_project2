package com.fc.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // step0 뷰 초기화
        initOnOffButton()
        initChangeAlarmTimeButton()

        // step1 데이터 가져오기
        val model = fetchDataFromSharedPreferences()
        renderView(model)

        // step2 뷰에 데이터 그리기
    }

    private fun initChangeAlarmTimeButton() {
        val changeAlarmBtn = findViewById<Button>(R.id.changeAlarmBtn)
        changeAlarmBtn.setOnClickListener {
            // 현재 시간을 가져온다.
            val calendar = Calendar.getInstance()
            // TimePickerDialog 사용
            TimePickerDialog(this,{ picker, hour, minute ->
                //데이터 저장하고 뷰를 업데이트
                val model = saveAlarmModel(hour, minute, false)
                renderView(model)
                // 기존 알람삭제
                cancelAlarm()
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show()
        }
    }
    private fun saveAlarmModel(hour: Int, minute: Int, onOff: Boolean): AlarmDisplayModel{
        val model =  AlarmDisplayModel(hour, minute, onOff)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        with(sharedPreferences.edit()){
            putString(ALARM_KEY, model.makeDataForDB() )
            putBoolean(ON_OFF_KEY, model.onOff )
            commit()
        }

        return model
    }
    private fun initOnOffButton() {
        val onOffBtn = findViewById<Button>(R.id.onOffBtn)
        onOffBtn.setOnClickListener {
            // 데이터를 확인한다.
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener
            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff.not())
            renderView(newModel)

            // 온오프에따라 작업을 처리한다.
            if(newModel.onOff) {
                // 켜진 경우 - 알람등록
                val calendar = Calendar.getInstance().apply{
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if(before(Calendar.getInstance())){
                        add(Calendar.DATE, 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else{
                // 알람 제거
                cancelAlarm()
            }
        }
    }

    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ON_OFF_KEY, false)
        val alarmData = timeDBValue.split(":")

        val model = AlarmDisplayModel(alarmData[0].toInt(), alarmData[1].toInt(), onOffDBValue)

        // 보정 예외처리
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
        if(pendingIntent == null && model.onOff){
            // 알람은 꺼져있는데, 데이터가 알람이 있는 경우
            model.onOff = false
        } else if(pendingIntent != null && model.onOff.not()){
            // 알람은 있는데, 데이터에는 알람이 등록이 안되있는 경우
            pendingIntent.cancel()
        }
        return model
    }

    private fun renderView(model: AlarmDisplayModel){
        findViewById<TextView>(R.id.ampmTv).apply{
            text = model.amPmText
        }
        findViewById<TextView>(R.id.timeTv).apply{
            text = model.timeText
        }

        findViewById<Button>(R.id.onOffBtn).apply{
            text = model.onOffText
            tag = model
        }

    }
    private fun cancelAlarm(){
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }
    companion object{
        private const val ALARM_KEY = "alarm"
        private const val ON_OFF_KEY = "onOff"
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_REQUEST_CODE = 1000

    }

}