Youtube app
===
## **MotionLayout** 사용하기 [공식문서](https://developer.android.com/training/constraint-layout/motionlayout?hl=ko)
> ConstraintLayout 라이브러리의 일부 (서브 클래스)  
> 레이아웃 전환과 UI 이동, 크기 조절 및 애니메이션에 사용
> *OTT 앱 인트로 따라하기* 에서 더 자세히 후술
>
>
>
>  이 프로젝트에서 사용  
>  메인 액티비티와 동영상 프래그먼트간 다음과 같은 화면 전환을 위해 모션 레이아웃이 사용되었다.  
> ![KakaoTalk_20221013_153648800](https://user-images.githubusercontent.com/79445881/195521843-743f6b14-2c18-43bb-b1da-b947835f8a8b.gif)
>

```xml
<androidx.constraintlayout.motion.widget.MotionLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mainMotionLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layoutDescription="@xml/activity_main_scene"
    tools:context=".MainActivity">
    
    ...

</androidx.constraintlayout.motion.widget.MotionLayout>
motion layout을 만들고  app:layoutDescription="@xml/activity_main_scene" 를
통해 해당 xml에서 constraint 및 transition을 지정하여 애니메이션을 사용할 수 있음
```
[xml-1](https://github.com/Kim-Min-Jong/android_practice_project2/blob/Upper_intermediate/upper%20intermediate/YouTube/app/src/main/res/xml/activity_main_scene.xml)
[xml-2](https://github.com/Kim-Min-Jong/android_practice_project2/blob/Upper_intermediate/upper%20intermediate/YouTube/app/src/main/res/xml/fragment_player_scene.xml)

> 그리고, 해당 프로젝트에서 메인 액티비티에서 FrameLayout(영상 재생 프래그먼트 컨태아너) 이 레이아웃 전체를 잡아먹어  
> 메인 액티비티의 recycler view가 동작을 하지 않았는데, 프래그먼트를 커스텀 motion layout으로 변환하여
> 코드를 통해 터치이벤트, 모션이벤트를 지정해주어 메인 액티비티의 recycler view 동작을 가능하게 하였다.
```kotlin
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
```


## **Exoplayer** 사용하기 [공식문서](https://exoplayer.dev/hello-world.html)
> Google이 Android SDK 와 별도로 배포되는 오픈소스 프로젝트이다.
> 오디오 및 동영상 재생 관련 강력한 기능들 포함하고 있다.
> 유튜브 앱에서 사용하는 영상 재생 라이브러리이다.  
> 별 다른 설정 없이도 네트워크로부터 미디어를 스트리밍 형태로 불러와 재생할 수도 있고 다양한 포맷들을 지원하먀 커스터마이징도 지원한다.
>
> 지원하는 다양한 포맷들이 있다. 이 [링크](https://exoplayer.dev/supported-formats.html) 를 확인하면 된다.
>

> **주요 컴포넌트**
> ExoPlayer를 사용하기 위한 주요 컴포넌트들이 있다.
> > ```View```: 미디어파일을 불러와 실졔 UI에 적용시키는 요소로, xml로 선언하고 ExoPlayer와 바인딩하여 사용한다.  
> > ```ExoPlayer```: 미디어 파일을 화면에 보여주는 중요한 컴포넌트이다.
> > ```DataSourceFactory```: ```MediaSource```를 생성할 때 ```DataSourceFactory```를 넣어줘야한다.
> > ```DataSource```는 URI로 부터 데이터를 읽는데 사용된다.   
> > ```MediaItem```: ```MediaItem```은 media를 재생하는 항목으로 URI를 기반으로 생성한다.
> > 실제 재생을 하려면 ```MediaItem```으로 MediaSource를 만들어야한다.  
> > ```MediaSource```: ExoPlayer에서 실제 재생을 하려면 MediaSource가 필요하다.  
> > ```MediaSource```는 ```MediaItem```을 이용하여 생성하고, ExoPlayer에 연결시겨 사용한다.


> 기본 사용법
>
>
> 먼저, 외부라이브러리이므로 implementation을 한다.   
> ``` implementation 'com.google.android.exoplayer:exoplayer:2.18.1'```
>
>
> 그리고, ExoPlayer를 선언해준다.
> ```kotlin 
> private var player: ExoPlayer? = null
>  ```
>
> 그리고 ExoPlayer를 null이 아닌 실제 객체로 만들어주고 뷰에 바인딩해준다.
> ```kotlin
> context?.let {
>      player = ExoPlayer.Builder(it).build()
>  }
>
> binding?.playerView?.player = player
> ```
>
>
> 그리고, play할 데이터를 넣어준다.
> ```kotlin
>    fun play(url: String, title: String) {
>        context?.let {
>            val dataSourceFactory = DefaultDataSource.Factory(it)
>            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
>                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
>            player?.setMediaSource(mediaSource)
>            player?.prepare()
>            player?.play()
>        }
>    }
> ```
>
>
> ExoPlayer는 앱을 밖으로 나가도 계속 실행되므로 생명주기에 맞춰 일시정지, 해제등을 지정해준다.
> ```kotlin
> override fun onStop() {
>    super.onStop()
>    player?.pause()
> }
>
> override fun onDestroy() {
>    super.onDestroy()
>    player?.release()
> }
>```


---

### Youtube

Retrofit 을 이용하여 영상 목록을 받아와 구성함

MotionLayout 을 이용하여 유튜브 영상 플레이어 화면전환 애니메이션을 구현함.

영상 목록을 클릭하여 ExoPlayer 를 이용하여 영상을 재생할 수 있음.
