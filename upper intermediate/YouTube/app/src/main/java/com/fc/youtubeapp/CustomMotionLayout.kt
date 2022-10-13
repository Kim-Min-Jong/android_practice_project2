package com.fc.youtubeapp

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

// 모션레이아웃을 재정의하여서 프래그먼트가 터치가 안되었던 점을 해결
class CustomMotionLayout(context: Context, attrs: AttributeSet? = null): MotionLayout(context, attrs) {
    private var motionTouchStarted = false
    private val mainContainerView by lazy{
        findViewById<View>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    //제스처 리스너
    private val gestureListener by lazy{
        // 목록 리사이클러뷰에 스크롤 제스처 이벤트시 실행
        object: GestureDetector.SimpleOnGestureListener(){
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                mainContainerView.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(), e1.y.toInt())
            }
        }
    }
    // 제스처 감지기
    private val gestureDetector by lazy{
        GestureDetector(context, gestureListener)
    }

    init {
        // 트랜지션이 완료되면 motionTouchStarted도 끝난것이기 때문에 false로 바꿈
        setTransitionListener(object:TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}

        })
    }


    // 레이아웃의 터치 이벤트가 발생되면 실행
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked){
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event)
            }
        }
        // 터치가 시작이 안되었으면 어느위치에서 터치가 시작이 되는지 가져옴
        if(!motionTouchStarted) {
            // 어느위치에서 터치가 시작이 되는지 가져옴
            mainContainerView.getHitRect(hitRect)
            // 실제 터치 이벤트의 좌표가 hitRect안에 있는지 확인하여 true false 결정
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
        }
        return super.onTouchEvent(event) && motionTouchStarted
    }

    // 제스처 이벤트 감지되면 실행
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}