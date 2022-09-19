Push Alarm Reciever
===

## Firebase
> **Firebase 간단소개**  
> 파이어베이스는 구글이 소유하고 있는 모바일 애플리케이션 개발 플랫폼으로,
>  파이어베이스를 활용하여 앱을 개발하고, 개선할 수 있다.
> 정리해서 말하면 파이어베이스는 *앱을 개발하고 개선하고, 키워 갈 수 있는 도구 모음이며,*
> *인증, 데이터베이스, 구성 설정, 파일저장, 푸시 메세지등 다양한 API를 가지고 있어 개발자들이 직접 해당기능을*
> *구현하지 않아도되어 생산성 향상뿐 아니라 앱 개발에 있어서 개발자가 사용자 경험에 집중 할 수 있게 해준다.*

## setting
> Firebase setting 과정
>>1. [firebase 문서](https://firebase.google.com/products-build?authuser=0&hl=ko) 에 접속하여 '시작하기'를 통해 프로젝트 추가
>>2. 프로젝트 명명후 다음단계로 진입
>>3. 애널리틱스 설정 후 프로젝트 진입
>>4. 파이어베이스 프로젝트에서 안드로이드 앱 생성
>> 5. 앱 패키지명 등록 후 안드로이드 프로젝트에 google-service.json 복사
>> 6. 그 후 단계에서 명시되어 있는 종속성을 앱수준의 gradle에 입력 후 build
>
> 를 모두 수행하면 기본적인 세팅이 끝난다.

+ Cloud Messaging
  > Firebase 클라우드 메시징(FCM)은 메시지를 안정적으로 무료 전송할 수 있는 크로스 플랫폼 메시징 솔루션이다.  
  > 클라우드 메시징을 사용하면 새 메일이나 알림등을 클라이언트앱에 알릴 수 있다.  
  > **주요기능**  
  > 1.*알림 메시지 or 데이터 메시지 전송*: 사용자에게 표시되는 알림 메시지를 전송합니다. 또는 데이터 메시지를 전송하고 애플리케이션 코드에서 임의로 처리한다.  
  > 2.*다양한 메시지 타겟팅*: 단일 기기, 기기 그룹, 주제를 구독학 기기 등 3가지 방식으로 클라이언트 앱에 메시지를 전달할 수 있다.  
  > 3.*클라이언트 앱에서 메시지 전송*: 앱에서 파이어베이스 서버로 확인, 채팅 ,기타 메시지를 보낼 수 있다.

  > **메시지 처리 과정**
  >
  > ![](https://firebase.google.com/static/docs/cloud-messaging/images/diagram-FCM.png?hl=ko)
  > 1.환경구성  
  > 2.FCM 백엔드로 메세지 전송  
  > 3.백엔드에서 처리된 메시지를 기기에 라우팅하고 전송, 필요 시 플랫폼별 구성을 적용  
  > 4.사용자에게 표시
  >

  > **메세지 유형**
  > > *알림 메시지*
  > > + FCM이 클라이언트 앱을 대신해 최종 사용자 기기에 자동으로 메시지를 표시한다.
  > > + 사용자에게 표시되는 키모음이 미리 정의되어 있다.
  > > + 앱이 백그라운드 상태이면 알림 메시지가 알림 목록으로 전송된다. 포그라운드 상태의 앱인 경우 콜백 함수가 메시지를 처리한다.
  >
  > > *데이터 메시지*
  > > + 클라이언트 앱이 테이터 메시지 처리를 한다.
  > > + 데이터 메시지에는 커스텀 키-값 쌍만 존재한다.(사전정의 없음)
  > > + 클라이언트 앱이 콜백 함수의 데이터 페이로드를 수신한다.

  > **메시지 수신**  [문서](https://firebase.google.com/docs/cloud-messaging/android/receive)  
  > 안드로이드 앱에서 메시지를 수신하려면 ```FirebaseMessagingService```를  
  > 확장하는 서비스를 사용해야 한다. 서비스에서는 ```onMessageRecieved```와 ```onDeletedMessaged```
  > 콜백을 오버라이딩해야한다. 또, 모든 메시지는 20초이내에 처리되어야 한다.  
  > ```onMessageRecieved```는 다음 경우를 제외하고 대부분의 메시지 유형에 제공된다.
  >
  > *1.앱이 백그라운드 상태일 때 전송된 알림 메시지: 이 경우 알림이 기기의 작업 표시줄로 전송됩니다. 사용자가 알림을 탭하면 기본적으로 앱 런처가 열립니다.*
  >
  > *2.알림과 데이터 페이로드가 둘 다 포함된 메시지(백그라운드에서 수신된 경우): 이 경우 알림은 기기의 작업 표시줄로 전송되고 데이터 페이로드는 런처 활동의 인텐트 부가 정보로 전송됩니다.*
  >

    + 등록 토큰 액세스
      >  특정 기기로 메시지를 보내려면 기기의 등록 토큰을 알아야 합니다. 알림 콘솔의 필드에 토큰을 입력해야 이 서비스를 이용할 수 있으므로 토큰을 검색한 후 복사하거나 저장해야한다.
      >
      >  또, 토큰은 자주 변경이 될 수 있다.  
      ```ex) 새 기기에서 앱 복원, 사용자가 앱 제거/재설치, 사용자가 앱 데이터 소거```  
      그렇기 떄문에, 현재 토큰을 가져와서 사용하는 것은 ,라이브 서비스와 같은 대형 서비스 유지시
      어려운 점이 있다. 그래서 라이브 서비스등을 구성 할 때는 ```FirebaseMessagingService```를 확장하고 ```onNewToken```을 재정의하여 이 토큰에 액세스
      하는 것이 안전하다.
      > > 서비스 확장시 androidmainfest.xml에 반드시 등록을 해주어야한다.

  ```kotlin
  class MyFirebaseMessagingService: FirebaseMessagingService() {
  override fun onNewToken(token: String) {
  super.onNewToken(token)
  }
  
      override fun onMessageReceived(message: RemoteMessage) {
          super.onMessageReceived(message)
      }
  }
  ```
  ```xml
  <service android:name=".MyFirebaseMessagingService"
        android:exported="false">
         <intent-filter>
               <action android:name="com.google.firebase.MESSGING_EVENT"/>
         </intent-filter>
  </service>
  ```




+ Notification
  > notification은 사용자에게 미리 알림을 주고 다른사람과 소통을 가능하게 하며  
  > 앱에서 보내는 기타 정보를 적시에 제공하기 위해 안드로이드가 앱의 UI 외부에  
  > 표시하는 메시지이다. (여기서는 파이어베이스서버로 부터 메시지를 수신한다.)  
  > 사용자는 notification을 탭하여 앱을 열거나 알림에서 바로 특정 작업을 할 수 있다.
  > ```Notification```클래스와 그 하위 클래스들을 사용하여 구현한다.

  ##-
    + notification 만들기
  ```kotlin
  val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)
  val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        // notification 의 아이콘
        .setSmallIcon(R.drawable.ic_notifications_24)
        // notification의 제목
        .setContentTitle(title)
        // notification의 내용
        .setContentText(msg)
        // notification의 우선순위
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // notification을 눌렀을떄 실행할 intent -- 추후 추가 기술
        .setContentIntent(pendingIntent)
        // notification 터치시 자동으로 notification 제거
        .setAutoCancel(true)
  ```
  >  NotificationCompat.Builder객체를 통하여 만든다.  
  >  *setPriorty*: android 7.1이하에서 얼마나 강제적이여야하는지 결정 (8.0 이상의 경우 채널 중요도로 설정해야함)


> **android 8.0이상에서는 notification을 제공하려면 반드시 앱의 notification channel을 구분지어야한다.**  
>  그래서, 앱의 notification channel을 먼저 앱에 등록해야한다.
  ```kotlin
  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
              CHANNEL_ID,
              CHANNEL_NAME,
              NotificationManager.IMPORTANCE_DEFAULT
      )
      channel.description = CHANNEL_DESCRIPTION
  
      (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
              .createNotificationChannel(channel)
    }
}
  ```
> 안드로이드 8.0(Oreo) 부터 channel을 적용해야하니 system sdk version을 확인한다.  
> 그리고 ```NotificationChannel```클래스로 ```NotificationChannel``` 객체를 만들고  
> ```NotificationManager```의 ```createNotificationChannel```메소드를 통해 채널을 생성한다.

> 이제, notification을 터치했을 때의 동작 설정에 대해 알아본다.
> notification을 터치하면 그에 따른 앱이 실행되거나 특정 작업을 수행해야 할 것이다.
> 이때, 앱이 켜지는 등 intent의 변화가 있을 것이다. [위의코드](#-)를 보면 pendingIntent로 넘겨주는 것을 볼 수 있는데,  
> notification의 특성을 보면 intent가 아닌 pendingIntent로 넘겨주는지 알 수 있다.
>
>
> notification의 사용경로를 보면 푸시알림이 굉장히 많다.
> 만약, A 앱을 사용하는 중, B 앱에서 푸시알림이 왔다고 가정하자. 이때, 알림을 누르면 B앱으로 넘어가야 할 것이다.
> 그런데, 여기서 푸시알림의 intent 방식을 일반 intent로 해놓았다면 정상동작하지 않는다.
> A 앱으로 부터 B 앱의 intent로는 이동이 불가능하기 때문이다.  
> 이때, pendingIntent를 사용하는데, 간단하게 설명하면 pendingIntent는 intent가 정의된 앱이 아닌, 다른 앱에서 intent를 실행하도록 하는 intent이다.  
> pendingIntent를 사용함으로써, 안전한 앱의 실행을 보장할 수 있다.
>(*FLAG_UPDATE_CURRENT*를 통해 pendingIntent를 하나만 유지하고 이전 intent는 변경해버린다.)


+ notification 표시
```kotlin
NotificationManagerCompat.from(Context)
        .notify(type.id, createNotification(type, title, msg))
```
> ```NotificationManagerCompat```의 ```notify```를 통해 알림을 표시한다.

* notification 스타일
    + 일반 알림
  > [link](#-) 이 설정이 일반 알림이다.
    + 확장형 알림, 커스텀 알림
        * 공통
          >```notificationBuilder.setStyle(param)```을 통해 원하는 스타일을 추가한다.
        * 확장형
          > ```setStyle()```의 파라미터에 ```NotificationCompat.BigTextStyle().bigText(text)```를 추가하여
          > 알림창을 확대 시킬 수 있는 확장형 알림을 만들 수 있다.
        * 커스텀
          >```setStyle()```의 파라미터에 ```NotificationCompat.DecoratedCustomViewStyle()``` 커스텀을 알리고,
          >  ```notificationBuilder```에 ```setCustomContentView()```를 추가하여 커스텀 레이아웃을 만들어
          > 알림창에 띄울 수 있다.
          ```kotlin
            setCustomContentView(
                  RemoteViews(
                       packageName,
                       R.layout.view_custom_notification
                  )
          ```