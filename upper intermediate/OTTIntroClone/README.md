OTT Intro Clone
===

## custom appbar
> 화면의 상단은 창에서 기본으로 제공하는 Material 의 ActionBar 혹은 뷰의 일종으로 커스텀 할 수 있는 Toolbar로 구성할 수 있다.
> Toolbar 등은 타이틀 문자열, 앱 아이콘 이미지, 메뉴 등을 구성하는 것이 주목적이다.
>
> AppBarLayout은 이 Toolbar를 포함하여 Toolbar 이외에 액티비티 상단을 조금 더 넓게 구성하거나 이미지를 포함하는 등 다양하게 구성할 수 있습니다.
>
> AppBarLayout에서 커스텀을 할 때, 스크롤을 하면 appbar가 사라지거나 변화하는 효과를 주고싶을 때가 있는데, 이때
> CollapsingToolbarLayout을 사용한다.
> CollapsingToolbarLayout은 동적인 상단바를 만드는데 사용된다.
> CollapsingToolbarLayout을 사용하기 위해서는 CoordinatorLayout이 상위 레이아웃으로 반드시 있어야 한다.
>
> CoordinatorLayout는 자식뷰에 behavior를 지정할 수 있는데,
> behavior는 스크롤, 드래그, 스와이드, 플링 등, 뷰의 다양한 움직임이나 애니메이션에 따른 상호작용을 구현하기 위해 사용된다.
> 이 behavior 속성을 사용하여 스크롤 시에 상단바가 반응하도록 만들어야 하므로, 반드시 CoordinatorLayout을 사용해야 한다.
>
> ```xml
> <androidx.coordinatorlayout.widget.CoordinatorLayout>
>   <com.google.android.material.appbar.AppBarLayout>
>       <com.google.android.material.appbar.CollapsingToolbarLayout>
>              <androidx.appcompat.widget.Toolbar>
>                   <FrameLayout>
>                       <androidx.constraintlayout.widget.ConstraintLayout>
>
>                      </androidx.constraintlayout.widget.ConstraintLayout>
>                 </FrameLayout>
>             </androidx.appcompat.widget.Toolbar>
>         </com.google.android.material.appbar.CollapsingToolbarLayout>
>     </com.google.android.material.appbar.AppBarLayout>
> </androidx.coordinatorlayout.widget.CoordinatorLayout>
> <!-- ...계층구조 사이에 필요한 뷰 넣기 -->
> ```
>
>
> ```androidx.coordinatorlayout.widget.CoordinatorLayout```: ```CollapsingToolbarLayout```을
> 사용하기 위해 선언해야하는 상위 레이아웃이다. behavior를 통해 움직임, 애니메이션을 지정할 수 있다.
>
> ```com.google.android.material.appbar.AppBarLayout```: 앱바의 최상단 레이아웃을 지정한다.
>
> ```com.google.android.material.appbar.CollapsingToolbarLayout```: 스크롤시 동적인 움직임을 나타내는 앱바를 만들기 위해 사용한다.
>
> ```androidx.appcompat.widget.Toolbar```: 스크롤 후에도 계속 남아있을 기본 툴바(앱바)를 정의한다. (하위 레이아웃에 컴포넌트 정의)

```kotlin
    // 스크롤 시에 앱바를 바뀌게한다.
    private fun initAppBar() {
        // 앱바의 상태 변경을 감지해서 스크롤시 일정 스크롤을 넘어가면 alpha값을 바꾸어 투명/불투명하게 한다.
        binding?.appBar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this)
            // 실제 스크롤된 값 계산
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            // 스크롤시 offset값 계산
            val realAlphaVerticalOffset = if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            //300dp 이상 스크롤이 되면 alpha값을 0으로 만들어 앱바를 투명하게
            if (abstractOffset < topPadding) {
                binding?.toolbarBackgroundView?.alpha = 0f
                return@OnOffsetChangedListener
            }
            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            // 스크롤이 된 percentage를 계산하여 스크롤된 정도의 alpha값을 계속 바꿔줌
            binding?.toolbarBackgroundView?.alpha = 1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }
    
    // 앱바 기본 초기화
    private fun initActionBar() = with(binding) {
        // 아이콘 제거
        this?.toolbar?.navigationIcon = null
        // 액션바(앱바)의 좌우 여백 없애기
        this?.toolbar?.setContentInsetsAbsolute(0, 0)
        // 액션바 지정
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.let {
            // 액션바 설정
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

```


## Motion Layout
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Upper_intermediate/upper%20intermediate/YouTube#motionlayout-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C)

> MotionLayout은 ConstraintLayout2.0부터 추가된 ConstraintLayout의 서브 클래스이다.  
> 레이아웃에 있어서, 애니메이션에 중점을 둔 레이아웃이다. MotionScene이라는 xml을 이용하여 layout의 컴포넌트들의 움직임을 제어할 수 있다.
> API 버전 14부터 호환이된다.
> 최근의 안드로이드 스튜디오에서는 직접 xml코드를 치는 것이 아닌 GUI 툴이 있어서, 편의성이 높아졌다.

> MotionLayout의 특징
> > 1. ConstraintLayout을 상속받은 ViewGroup이다.
> > 2. animation, trasitionManager, CoordinatorLayout의 혼합체로 볼 수 있다.
> > 3. TransitionManager왁 같이 두 layout 사이의 애니메이션을 처리할 수 있다.
> > 4.  CoordinatorLayout같이 터치에 따라 반응하는 애니메이션을 구성할 수 있다.
> > 5. Declarative하다. 애니메이션에 관한 모든것을 xml로 처리할 수 있다.
> > 6. TransitionManager 는 Nested Layout 이나 Activity Transition 에도 사용가능하나, MotionLayout 은 직접적인 하위 view 들에게만 적용 가능하다.

> MotionLayout 은 MotionScene(xml)을 생성하고, 애니메이션 관련된 모든 것은 이 MotionScene(xml) 에 정의된다.
>
> ```ConstraintSet``` 은 Constraint 정의를 위한 별도의 layout xml 파일을 만들 필요 없이 직접 Constraint 를 정의할 때 사용할 수 있다. 타겟 뷰의 크기와 위치를 선언할 수 있으며, CustomAttribute 를 통해 타겟 뷰의 다른 속성들도 정의할 수 있다.
>
> ```Transition``` 은 애니메이션의 시작과 끝 Constraint 를 정의하고, duration 등의 기타 애니메이션 속성들을 정의한다. 사용자와 상호작용할 수 있는 OnClick 과 OnSwipe 핸들러를 등록할 수 있으며, KeyFrameSet 을 통해 애니메이션의 시작과 끝 뿐만 아니라 그 사이 지점들의 Constraint 를 정의할 수 있다.


> 기본 사용법
> ```xml
>  <androidx.constraintlayout.motion.widget.MotionLayout
>     android:id="@+id/buttonShownMotionLayout"
>     android:layout_width="match_parent"
>     android:layout_height="match_parent"
>     app:layoutDescription="@xml/button_shown_scene"
>     app:layout_behavior="@string/appbar_scrolling_view_behavior">
>     ... 하위 컴포넌트 지정
> </androidx.constraintlayout.motion.widget.MotionLayout>
> ```
>
> MotionLayout을 선언한다. ```app:layoutDescription```은 애니메이션을 지정할 MotionScene이다.
>
>  ```xml
> <?xml version="1.0" encoding="utf-8"?>
> <MotionScene xmlns:android="http://schemas.android.com/apk/res/android"
>  xmlns:app="http://schemas.android.com/apk/res-auto"
>  xmlns:motion="http://schemas.android.com/tools">
>
>     <Transition
>        app:constraintSetEnd="@id/end"
>         app:constraintSetStart="@+id/start"
>        app:duration="500">
>
>         <KeyFrameSet>
>
>             <KeyAttribute
>                android:alpha="0"
>                 motion:framePosition="0"
>                 motion:motionTarget="@id/button"
>                motion:transitionEasing="decelerate" />
> 
>             <KeyAttribute
>                 android:alpha="1"
>                 motion:framePosition="100"
>                 motion:motionTarget="@id/button"
>                 motion:transitionEasing="decelerate" />
>
>         </KeyFrameSet>
>     </Transition>
>
>     <ConstraintSet android:id="@+id/start">
>         <Constraint
>             android:id="@+id/button"
>             android:layout_width="0dp"
>             android:layout_height="64dp"
>             android:layout_marginStart="24dp"
>             android:layout_marginEnd="24dp"
>             app:layout_constraintBottom_toBottomOf="parent"
>             app:layout_constraintEnd_toEndOf="parent"
>             app:layout_constraintStart_toStartOf="parent"
>             app:layout_constraintTop_toTopOf="parent"
>             app:layout_constraintVertical_bias="1.4" />
>     </ConstraintSet>
>
>     <ConstraintSet android:id="@+id/end">
>         <Constraint
>             android:id="@id/button"
>             android:layout_width="0dp"
>             android:layout_height="64dp"
>             android:layout_marginStart="24dp"
>             android:layout_marginEnd="24dp"
>             app:layout_constraintBottom_toBottomOf="parent"
>             app:layout_constraintEnd_toEndOf="parent"
>             app:layout_constraintStart_toStartOf="parent"
>             app:layout_constraintTop_toTopOf="parent"
>             app:layout_constraintVertical_bias="0.97" />
>     </ConstraintSet>
> </MotionScene>
> ```
>
> ```<Transition>```은 애니메이션의 시작과 끝을 지정한다. id값을 통해서 start, end를 지정하고, duration을 통해 애니메이션 실행시간을 지정한다.
>
> ```<KeyFrameSet>```은 CustomAttribute을 지정할 때 사용한다. 특정 타겟뷰를 KeyAttribute로 지정하여 애니메이션을 지정할 수 있다.
>
> ```<ConstraintSet>```은 <Transition>에서 지정한 id값을 통해 start 상태와  end상태 일때의 뷰의 상태(Constraint)를 지정한다.
> 여기서는 버튼의 bias를 변경해서 버튼의 위치를 옮겨주는 효과를 주었다.
>
> 버튼 뿐만 아니라 MotionLayout의 하위 뷰(텍스트,이미지)들을 넣고, 그것들의 속성을 Constraint로 조정하면
> 애니메이션 동작을 만들 수 있다.
>
> 코드를 통해 애니메이션을 조작 할 수도 있다.
> ```kotlin
> //ex
> 
> // setTransition 함수를 통해 <Transition>을 통한 지정이 아닌 특정 ConstraintSet끼리의 애니메이션을 지정할 수 있다.
> binding?.curationAnimationMotionLayout?.setTransition(R.id.curation_animation_start1, R.id.curation_animation_end1)
> 
> // transition의 상태를 end로 바꾼다.
> binding?.curationAnimationMotionLayout?.transitionToEnd()
> 
> // transition의 상태를 start로 바꾼다.
> binding?.buttonShownMotionLayout?.transitionToStart()
> 
> 
> // TransitionListener 오버라이드를 통한 트랜지션 변화에 따른 로직설정도 가능하다.
> binding?.gatheringDigitalThingsLayout?.setTransitionListener(object :
>            MotionLayout.TransitionListener {
>             // 트랜지션(움직임)이 사작되었을 때
>            override fun onTransitionStarted(
>                motionLayout: MotionLayout?,
>                startId: Int,
>                endId: Int
>            ) {
>                // 로직
>            }
>            // 트랜지션이 진행 중 일 때(상태 변화 중)
>            override fun onTransitionChange(
>                motionLayout: MotionLayout?,
>                startId: Int,
>                endId: Int,
>                progress: Float
>            ) {
>                // 로직
>            }
>           
>           // 트랜지션이 끝났을때
>            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
>                // 로직
>            }
>
>             // 트리거가 발동되었을 때
>            override fun onTransitionTrigger(
>                motionLayout: MotionLayout?,
>                triggerId: Int,
>                positive: Boolean,
>                progress: Float
>            ) {
>                // 로직
>            }
>        })
> ```
>
> 추가적으로 MotionLayout에 대한 정리글이 있어 남겨본다. [링크](https://blog.gangnamunni.com/post/MotionLayout/)
>
>
>
>
>
> 추가  
> [NestedScrollView1](https://developer.android.com/reference/androidx/core/widget/NestedScrollView)  
> [NestedScrollView2](https://velog.io/@kimbsu00/Android-7)