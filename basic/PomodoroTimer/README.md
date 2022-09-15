뽀모도로 타이머
====

## Layout 사용하기
+ constraint layout
    - chain
  > constraint layout에서는 ViewGroup과 view 들이 constraint를 맺어 뷰의 위치를 결정짓는다.  
  > 이때, view끼리 상호 참조 연결을 했을 떄, view들을 어떤 방식으로 연결시켜 표현 할지를 결정하는 것을 chain이라 한다.    
  >   ![](https://developer.android.com/static/reference/androidx/constraintlayout/widget/resources/images/chains.png)
  >
  > 아래와 같은 chainStyle등이 있다.
  >
  > **CHAIN_SPREAD**: 뷰들을 골고루 펼쳐 여백을 같게 한다.(기본값)  
  > **CHAIN_SPREAD_INSIDE**: CHAIN_SPREAD와 비슷하지만 가장 외곽에 있는 뷰들은 부모 뷰와 여백이 없는 상태로 골고루 펼쳐진다.  
  > **CHAIN_PACKED**: 뷰들이 뭉치게 되고 부모뷰로부터의 여백을 같게 한다. 여백을 조정하고 싶다면 bias 조정을 통해 한쪽으로 치우치게 만들 수 있다.
  >
  > ![](https://developer.android.com/static/reference/androidx/constraintlayout/widget/resources/images/chains-styles.png)

+ seek bar
> seek bar는 사용자가 지정한 범위 내에서 값을 선택할 수 있도록 하는 막대 모양의 뷰이다.  
> 음량 조절, 필터 적용, 동영상등 시간, 값 조절등 다양한 곳에 사용된다.
> seek bar는 이벤트 리스너를 통해 조작할 수 있다.
>
>> **onProgressChanged**: 시크바를 조작하는 동안 발생하는 이벤트  
>> **onStartTrackingTouch**: 시크바를 처음 터치했을 때 발생하는 이벤트  
>> **onStopTrackinigTouch**: 시크바 터치가 끝났을 때 발생하는 이벤트
```kotlin
ex)
seekBar.setOnSeekBarChangeListener (
      object: SeekBar.OnSeekBarChangeListener {
           override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                //...
           }
           override fun onStartTrackingTouch(p0: SeekBar?) {
                //...
           }
          override fun onStopTrackingTouch(p0: SeekBar?) {
                 //...   
          }

      }
)
```
```xml
<SeekBar
        android:id="@+id/seekBar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:max="60" 시크바 최대값
        android:progressDrawable="@color/transparent" 바의 색상
        android:thumb="@drawable/ic_baseline_unfold_less_24" 바의 포인터
        app:tickMark="@drawable/drawable_tick_mark" 바 모양 />
```


## 타이머 사용하기
+ android countdown timer
> 안드로이드에서 기본으로 제공하는 카운트다운 타이머 클래스이다
> 쓰레드나 다른 것으로 따로 구현할 필요없이, 이 클래스를 사용할 수 있다.
```kotlin
object: CountDownTimer(initialMillis, 1000L){ 
  // initialMillis = 총 카운트다운 시간
  override fun onTick(p0: Long) {
    // 지정한 time tick마다 할 행동 정의
      // 여기서는 위에 CountDownTimer 두번째 인자인 1000L
  }

  override fun onFinish() {
        //타이머가 끝났을 때 행동 정의
  }
}
```

## 효과음 사용하기
+ android sound pool
> 안드로이드에서 기본으로 제공하는 오디오 관련 클래스이다.  
> 긴 오디오가 아닌 효과음 같은 짧은 길이의 오디오를 효과적으로 사용할 수 있게 되어있다.
```kotlin
// 기본 예제

// 빌더형태로 객체생성
private val soundPool = SoundPool.Builder().build()

id = soundPool.load(context, R.raw.timSounder_ticking, 1)
soundPool.play(id, 1f, 1f, 0, 0, 1f)
```
```kotlin
// 주요함수

// 로딩
soundPool.load(context, resourceId, priority=1)

// 시작  (아이디, 왼쪽소리볼륨, 오른쪽소리볼륨, 우선순위, 루프여부 -1=INF, 재생범위)
soundPool.play(resourceId, leftValue, rigthValue, priority, loop, rate)

// 일시 정지
soundPool.pause(resourceId)
soundPool.autoPause()

//다시 시작
soundPool.resume(resourceId)
soundPool.autoResume()

// 객체 제거
soundPool.release()
```