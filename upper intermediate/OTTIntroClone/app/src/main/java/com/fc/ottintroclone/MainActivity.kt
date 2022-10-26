package com.fc.ottintroclone

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fc.ottintroclone.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var isGatheringMotionAnimating = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        makeStatusBarTransparent()

        initAppBar()
        initInsetMargin()

        initMotionLayoutListener()

        initScrollViewListener()
    }

    private fun initMotionLayoutListener() {
        binding?.scrollView?.viewTreeObserver?.addOnScrollChangedListener {
            if (binding?.scrollView?.scrollY!! > 150f.dpToPx(this).toInt()) {
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
    }

    private fun initScrollViewListener() {
        binding?.gatheringDigitalThingsLayout?.setTransitionListener(object :
            MotionLayout.TransitionListener {
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

    // 스크롤 시에 앱바를 바뀌게한다.
    private fun initAppBar() {
        binding?.appBar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this)
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            val realAlphaVerticalOffset = if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if (abstractOffset < topPadding) {
                binding?.toolbarBackgroundView?.alpha = 0f
                return@OnOffsetChangedListener
            }
            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            binding?.toolbarBackgroundView?.alpha = 1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }

    private fun initActionBar() = with(binding) {
        this?.toolbar?.navigationIcon = null
        this?.toolbar?.setContentInsetsAbsolute(0, 0)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

    //inset 값을 조정함 (coordinator 레이아웃의 insets(save area의 margin)을 조정
    private fun initInsetMargin() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(this?.coordinator as View) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            toolbarContainer.layoutParams = (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, insets.systemWindowInsetTop, 0, 0)
            }
            collapsingToolbarContainer.layoutParams = (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, 0, 0, 0)
            }

            insets.consumeSystemWindowInsets()
        }
    }

    private fun Float.dpToPx(context: Context) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

    private fun Activity.makeStatusBarTransparent() =
        with(window) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}