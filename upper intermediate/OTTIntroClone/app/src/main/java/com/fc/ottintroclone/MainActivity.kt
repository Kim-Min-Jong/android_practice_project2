package com.fc.ottintroclone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.constraintlayout.motion.widget.MotionLayout
import com.fc.ottintroclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var isGatheringMotionAnimating = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.scrollView?.viewTreeObserver?.addOnScrollChangedListener {
            if (binding?.scrollView?.scrollY!! > 150f.dptoPx(this).toInt()) {
                if (isGatheringMotionAnimating.not()) {
                    binding?.gatheringDigitalThingsLayout?.transitionToEnd()
                    binding?.buttonShownMotionLayout?.transitionToEnd()
                }
            } else {
                if (isGatheringMotionAnimating.not()) {
                    binding?.gatheringDigitalThingsLayout?.transitionToStart()
                    binding?.buttonShownMotionLayout?.transitionToStart()
                }
            }
        }

        binding?.gatheringDigitalThingsLayout?.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                isGatheringMotionAnimating = true
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                isGatheringMotionAnimating = true
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                isGatheringMotionAnimating = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) = Unit

        })
    }

    fun Float.dptoPx(context: Context) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}