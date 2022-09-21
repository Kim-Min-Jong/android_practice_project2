package com.fc.alarmapp

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // step0 뷰 초기화
        initOnOffButton()
        initChangeAlarmTimeButton()

        // step1 데이터 가져오기


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


                                  // 기존 알람삭제

            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show()
        }
    }
    private fun saveAlarmModel(hour: Int, minute: Int, onOff: Boolean): AlarmDisplayModel{
        val model =  AlarmDisplayModel(hour, minute, onOff)
        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)

        with(sharedPreferences.edit()){
            putString("alarm", model.makeDataForDB() )
            putBoolean("onOff", model.onOff )
            commit()
        }

        return model
    }
    private fun initOnOffButton() {
        val onOffBtn = findViewById<Button>(R.id.onOffBtn)
        onOffBtn.setOnClickListener {
            // 데이터를 확인한다.


            // 온오프에따라 작업을 처리한다.

            //오프-알람제거 온-알람등록

            //데이터 저장
        }
    }
}