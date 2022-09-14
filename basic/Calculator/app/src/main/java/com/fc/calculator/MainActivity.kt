package com.fc.calculator

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.room.Room
import com.fc.calculator.model.History
import org.w3c.dom.Text
import kotlin.math.exp

class MainActivity : AppCompatActivity() {
    private val expressionTv : TextView by lazy{
        findViewById<TextView>(R.id.expresstion_tv)
    }

    private val resultTv : TextView by lazy{
        findViewById<TextView>(R.id.result_tv)
    }
    private val historyLayout: View by lazy{
        findViewById<View>(R.id.history_ll)
    }
    private val historyLinearLayout: LinearLayout by lazy{
        findViewById<LinearLayout>(R.id.history_list)
    }

    lateinit var db: AppDatabase

    private var isOperator = false
    private var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(this, AppDatabase::class.java, "historyDB").build()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun buttonClicked(v: View){
        when(v.id){
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.button_div -> operButtonClicked("/")
            R.id.button_mod -> operButtonClicked("%")
            R.id.button_multi -> operButtonClicked("X")
            R.id.button_sub -> operButtonClicked("-")
            R.id.button_plus -> operButtonClicked("+")

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun operButtonClicked(op: String){
        if(expressionTv.text.isEmpty()) return
        when{
            isOperator ->{
                val text = expressionTv.text.toString()
                expressionTv.text = text.dropLast(1) + op
            }
            hasOperator ->{
                Toast.makeText(this,"연산자는 한 번만 사용할 수 있습니다", Toast.LENGTH_SHORT).show()
                return
            }
            else ->{
                expressionTv.append(" $op")
            }
        }

        val ssb = SpannableStringBuilder(expressionTv.text)
        ssb.setSpan(ForegroundColorSpan(getColor(R.color.green)), expressionTv.text.length - 1, expressionTv.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        expressionTv.text = ssb
        isOperator = true
        hasOperator = true
    }
    fun numberButtonClicked(num: String){
        if(isOperator){
            expressionTv.append(" ")
        }
        isOperator = false
        val expressionText = expressionTv.text.split(" ")
        if(expressionText.isNotEmpty() && expressionText.last().length >= 15){
            Toast.makeText(this,"15자리까지만 사용할 수 있습니다", Toast.LENGTH_SHORT).show()
            return
        } else if(expressionText.last().isEmpty() && num == "0"){
            Toast.makeText(this,"0은 맨 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        expressionTv.append(num)

        //TODO 실시간으로 resultview 보이게 하기기
        resultTv.text = calculateExpression()
    }

   private fun calculateExpression():String{
       val expressionText = expressionTv.text.split(" ")
       if(hasOperator.not() || expressionText.size != 3){
           return ""
       } else if(expressionText[0].isNumber().not() || expressionText[2].isNumber().not()){
           return ""
       }

       val exp1 = expressionText[0].toBigInteger()
       val exp2 = expressionText[2].toBigInteger()
       return when(expressionText[1]){
           "+" -> (exp1 + exp2).toString()
           "-" -> (exp1 - exp2).toString()
           "X" -> (exp1 * exp2).toString()
           "/" -> (exp1 / exp2).toString()
           "%" -> (exp1 % exp2).toString()
            else -> ""
       }
   }
    fun resultButtonClicked(v: View){
        val expressionText = expressionTv.text.split(" ")

        if(expressionTv.text.isEmpty() || expressionText.size == 1){
            return
        }
        if(expressionText.size != 3 && hasOperator){
            Toast.makeText(this,"아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if(expressionText[0].isNumber().not() || expressionText[2].isNumber().not()){
            Toast.makeText(this,"오류가 발생되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val exprText = expressionTv.text.toString()
        val resultText = calculateExpression()

        // db입력 --- ui 스레드에선 안됨
        Thread(Runnable{
            db.historyDao().insertHistory(History(null, exprText, resultText))
        }).start()


        resultTv.text = ""
        expressionTv.text = resultText
        isOperator = false
        hasOperator = false
    }
    fun clearButtonClicked(v: View){
        expressionTv.text = ""
        resultTv.text = ""
        isOperator = false
        hasOperator = false
    }

    fun historyButtonClicked(v: View){
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()
        // db에서 기록 가져오기
        Thread(Runnable{
            db.historyDao().getAll().reversed().forEach {
                // binding은 UI이 이므로 UI 쓰레드에서 실행해야함
                runOnUiThread {
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row,null, false)
                    historyView.findViewById<TextView>(R.id.expresstion_tv).text = it.expression
                    historyView.findViewById<TextView>(R.id.result_tv).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()
    }

    fun closeHistoryButtonClicked(v: View){
        historyLayout.isVisible = false
    }
    fun historyClearButtonClicked(v:View){
        // db에서 모든 기록 삭제
        // 뷰에서 모든 기록 삭제
        historyLinearLayout.removeAllViews()
        Thread(Runnable{
            db.historyDao().deleteAll()
        }).start()
    }
    fun String.isNumber():Boolean{
        return try{
            this.toBigInteger()
            true
        } catch(e: Exception){
            false
        }
    }
}