package com.fc.voicerecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(
    context: Context,
    attrs: AttributeSet ?= null
): View(context, attrs) {

    var onRequestCurrentAmplitude: (()-> Int)? = null
    // 음향 진폭
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }
    private var drawingWidth = 0
    private var drawingHeight = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    private var isReplaying = false
    private var replayingPosition = 0


    private val visualizeRepeatAction: Runnable = object: Runnable{
        override fun run() {
            if(!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                // amplitude, drawing
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            }else{
                replayingPosition++
            }
            invalidate()
            handler?.postDelayed(this, ACTION_TERM)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingHeight = h
        drawingWidth = w
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2
        var offsetX = drawingWidth.toFloat()
        drawingAmplitudes.let {
            if(isReplaying){
                it.takeLast(replayingPosition)
            } else{
                it
            }
        }.forEach {
            val lineLength = it / MAX_AMPLITUE * drawingHeight * 0.8f

            offsetX -= LINE_SPACE
            if(offsetX < 0){
                return@forEach
            }

            canvas.drawLine(offsetX, centerY - lineLength / 2f, offsetX, centerY + lineLength / 2f, amplitudePaint)
        }
    }


    fun startVisualizing(isReplaying: Boolean){
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }
    fun stopVisualizing(){
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }
    fun clearVisualization(){
        drawingAmplitudes = emptyList()
        invalidate()
    }
    companion object{
        private const val LINE_WIDTH = 10f
        private const val LINE_SPACE = 15f
        private const val MAX_AMPLITUE = Short.MAX_VALUE.toFloat()
        private const val ACTION_TERM = 20L
    }
}