Voice Recoder
===

## Layout 사용하기
+ custom View
> 기존 정의되어 있는 view 이외의 뷰 만들기  
> 클래스를 만들고 특정 뷰를 상속받아 그 뷰의 기능을 하는 custom View를 만든다
```kotlin
class RecordButton(context: Context, attrs: AttributeSet): AppCompatImageButton(context, attrs) {
    fun updateIconWithState(state: State){
        when(state){
            State.BEFORE_RECORDING ->{
                setImageResource(R.drawable.ic_record_24)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop_24)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic__play_24)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop_24)
            }
        }
    }
}
```
> **canvas(onDraw)**
> custom View에서 원하는 UI를 그리고 싶을 때 가장 중요한 부분이다.
> **onDraw()** 메서드를 오버라이드하여 그리고 싶은 UI를 프로그래밍해야한다.
> 그리기 작업은 크게 ```Canvas```와 ```Paint```로 나뉜다.   
> ```Canvas```는 그리는 내용을 정의하고, ```Paint```는 그리기 방법을 정의한다.
> 예를 들면, 선을 그리고 싶을 때,```Canvas```는 선을 그리고 ```Paint``` 는 색상, 스타일 등을 정의한다.  
> **onDraw()** 메소드를 오버라이딩 할 때, 주의해야 할점은 이 메소드 안에서 객체를 만들지않아야 한다는 것이다.  
> 뷰를 다시 그릴 때 마다, onDraw가 불리고, 그때마다 객체가 계속 만들어져 메모리를 잡아먹을 수 있기 때문이다.
```kotlin
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    canvas.apply {
        // Draw the shadow
        drawOval(shadowBounds, shadowPaint)

        // Draw the label text
        drawText(data[mCurrentItem].mLabel, textX, textY, textPaint)

        // Draw the pie slices
        data.forEach {
            piePaint.shader = it.mShader
            drawArc(bounds,
                    360 - it.endAngle,
                    it.endAngle - it.startAngle,
                    true, piePaint)
        }

        // Draw the pointer
        drawLine(textX, pointerY, pointerX, pointerY, textPaint)
        drawCircle(pointerX, pointerY, pointerSize, mTextPaint)
    }
}
 ```


## Request runtime permissions (dangerous permission)
```
(Android 6.0 - 9) 사용자가 앱을 실행할 때 위험한 권한을 앱에 부여합니다. 권한 요청 시점(앱이 실행될 때 또는 사용자가 특정 기능에 액세스할 때 등)은 애플리케이션에 따라 다르지만 사용자가 특정 권한 그룹에 대한 애플리케이션 액세스를 부여/거부합니다. OEM/이동통신사는 앱을 사전 설치할 수 있지만, 예외 프로세스를 거치지 않는 이상 권한을 미리 부여할 수는 없습니다. (예외 생성 참고)

(Android 10-) 사용자가 개선된 투명성을 확인하고 어떤 앱이 활동 감지(AR) 런타임 권한을 보유하도록 할지 제어할 수 있습니다. 사용자에게는 항상 허용할지, 사용 중에만 허용할지 아니면 권한을 거부할지 묻는 런타임 권한 대화상자가 표시됩니다. OS가 Android 10으로 업그레이드되면 앱에 주어진 권한이 유지되지만 사용자가 설정으로 이동하여 권한을 변경할 수 있습니다.
런타임 권한은 앱이 사용자의 동의 없이 비공개 데이터에 액세스하지 못하도록 하지만 애플리케이션이 찾고 있거나 부여받은 권한 유형에 대한 추가적인 컨텍스트와 가시성을 앱에 제공합니다. 런타임 모델은 애플리케이션에 요청된 권한이 필요한 이유를 사용자가 이해하도록 돕고 사용자가 권한 부여 또는 거부에 대한 더 나은 결정을 내릴 수 있게 향상된 투명성을 제공하도록 개발자를 독려합니다.

출처: 공식 홈페이지 (런타임 권한)
```
```
권한을 받아오는 방식은
ActivityCompat.requestPermission를 사용
이전 방법과 동일
```

## Media Recorder
> 오디오 및 비디오 녹음을 할 수 있게 해주는 안드로이드 클래스 이다.
> encode decode 과정을 거치기 때문에, 지원하는 파일 형식이 따로 존재한다.
> 또, Media recorder 만읜 state를 갖기 때문에 동작하는데에 있어 state를 맞춰야한다.
>
> 상태 다이어그램
![](https://developer.android.com/static/images/mediarecorder_state_diagram.gif?hl=ko)


> 지원형식 및 코덱(링크 참조)  
> [링크](https://developer.android.com/guide/topics/media/media-formats?hl=ko#audio-codecs)

```kotlin
 recorder = MediaRecorder(Context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) // 마이크에 접근
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // 출력 포맷 지정
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // 인코더 지정
            setOutputFile(recordingPath) // 파일의 저장 경로
            prepare()  // 녹음 준비 완료
        }
        recorder?.start()



recorder?.run {
    stop()
    release()
}
```



## Media Player
> 오디오 및 비디오 파일을 실행할 수 있게 해주는 안드로이드 클래스이다.
> Media Recorder와 같이 자체 state가 있어 state에 따라 동작시켜야한다.
>
>
>![](https://developer.android.com/images/mediaplayer_state_diagram.gif)
```kotlin
        player = MediaPlayer().apply {
            setDataSource(recordingPath)
            prepare()
        }
        player?.start()


        player?.release()
```