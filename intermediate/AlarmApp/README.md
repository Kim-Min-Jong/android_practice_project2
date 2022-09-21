Alarm App
===
## AlarmManager 사용하기
> AlarmManager는 지정한 시간에 알람을 받을 수 있게하는 안드로이드 내장 기능이다.  
> 앱이 실행 중이 아닐 때라도 정한 시간에 이벤트를 받아 특정 작업을 처리할 수 있다.

> 알람에는 다음과 같은 특징이 있다.
> + 지정된 시간 또는 정해진 간격으로 인텐트를 실행한다.
> + BroadCast Receiver와 함께 사용하여 서비스, 인텐트를 실행하고 다른 작업을 실행할 수 있다.
> + 앱 외부에서 작동하므로 알람을 사용하면 앱이 실행중이 아닐 때도 이벤트나 작업을 받아 실행 할 수 있다.
> + 백그라운드를 사용하지 않아서, 리소스를 최소화한다.
>
> **주의점**
> + DOZE모드 일 떄는 AlarmManager가 작동하지 않는다.
>
> 잠자기 모드에서 실행을 시키려면 AlarmManager의 ```setAndAllowWhileIdle()```과 ```setExactAndAllowWhileIdle()```을 사용하여 알람을 실행할 수 있다.  
> 또 다른 옵션은 백그라운드 작업을 한 번 또는 주기적으로 진행하도록 구성된 새 WorkManager API를 사용하는 것이다.
>
> + 알람 트리거 사건을 정밀하게 설정하지 말아야한다.  
    > ```setRepeating()``` 대신 ```setInexactRepeating()```을 사용해야한다.


## Broadcast Reciever 사용하기
> Android 앱은 Android 시스템 및 기타 Android 앱에서 게시-구독 디자인 패턴과 유사한 브로드캐스트 메시지를 받거나 보낼 수 있다.  
> 관심 있는 이벤트가 발생할 때 이러한 브로드캐스트가 전송된다.  
> 예를 들어 Android 시스템은 시스템 부팅 또는 기기 충전 시작과 같은 다양한 시스템 이벤트가 발생할 때 브로드캐스트를 전송한다.  
> 또한 앱은 맞춤 브로드캐스트를 전송하여 다른 앱이 관심을 가질만한 사항(예: 일부 새로운 데이터가 다운로드됨)을 관련 앱에 알릴 수 있다.

> alarmManager는 앱 외부에서 작동한다고 하였으니 BroadCast Receiver를 통하여 이벤트를 받을 수 있다.

> **PendingIntent**
> Pending은 “보류하는” 이라는 뜻이다.  
> 이를 합쳐서 보면 “보류중인 intent”의미가 되는 PendingIntent가 나온다.  
> 의미대로 당장 사용하기 보다, 추후 특정 이벤트에서 발동되는 intent이다.  
> 목적은 외부 애플리케이션에 권한을 허가하여 안에 들어있는 Intent를 마치 본인 앱의 프로세스에서 실행하는 것처럼 사용하게 하는, 다른 컴포넌트에 위임처리를 하는 기능이다.  
> 미래 정해진 시각에 이벤트가 발생하는 AlarmManager에게 필수적인 요소이다.

> pendingIntent와 관련해서는 더 공부를 해봐야할 것 같다...

```kotlin
// alarmManager와 BroadCast Receiver 사용 

// 내장 서비스인 alarmManger 객체를 가져온다.
val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

// pendingIntent의 intent가 될 broadcast receiver intent를 생성한다.
val intent = Intent(this, AlarmReceiver::class.java)

// 위의 인텐트를 통해 pendingIntent를 만든다.(broadcast receiver로 실행된다.)
val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

// alarmManager가 InexactReating()메소드를 통해 반복적으로 실행된다.
alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
)


class AlarmReceiver : BroadcastReceiver() {
 // broadcast receiver가 실행되었을 시 동작하는 메소드이다.
  override fun onReceive(context: Context, intent: Intent) {
    // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    createNotificationChannel(context)
    notifyNotification(context)
  }
}
```

[일부 발췌(velog)](https://velog.io/@thevlakk/Android-AlarmManager-%ED%8C%8C%ED%97%A4%EC%B9%98%EA%B8%B0-1)


## Notification 사용하기
[복습 링크](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/PushAlarmReciever)


## Background 작업
+ Immediate Task(즉시 실행해야하는 작업)
    * Thread
    * Handler
    * kotlin Coroutines


+ Deffered Task(지연된 작업)
    * WorkManager


+ Exact Task(정시에 실행해야하는 작업)
    * AlarmManager



## AlarmManager
+ Real Time(실제 시간)으로 실행시키는 방법
> AlarmManager 설정시 triggerAtMillis 속성을 RTC_WAKEUP속성으로 지정하여 실제 시간 기반으로 알람을 실행한다.
+ Elapsed Time(기기가 부팅된지 얼마나 지났는지)으로 실행시키는 작업
> AlarmManager 설정시 triggerAtMillis 속성을 ELAPSED_REALTIME_WAKEUP으로 지정하여 부팅된 후 경과 시간을 기반으로 알람을 실행한다.


## 알람앱
지정된 시간에 알람이 울리게 할 수 있음  
지정된 시간 이후에는 매일 가튼 시간에 반복되게 알림이 울리게 할 수 있음