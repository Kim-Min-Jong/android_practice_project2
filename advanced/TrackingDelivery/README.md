Tracking Delivery
===

## MVP
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/ToDoList#mvp-architecture)
![출처:https://frtt0608.tistory.com/94](https://blog.kakaocdn.net/dn/5DXdG/btq5rPtNxxO/udhr8GwRs3T2IHyIAEkZcK/img.png)
Model
- 내부 데이터를 저장하고 처리하는 데이터 클래스
- View, Presenter에 의존적이지 않은 독립적인 영역

View
- UI를 보여주는 부분 (activity, fragment)
- Model에서 처리한 데이터(api call 등)를 Presenter를 통해 받아 UI에 보여줌
- action 및 lifecycle을 확인하며 Presenter에 이벤트를 전달
- Presenter에 의존적임 (데이터를 주고 받음)

Presenter
- Model - View 사이의 매개체
- Model과 View를 매개체라는 점에서 Controller와 유사하지만, View에 직접 연결되는 대신 인터페이스를 통해 상호작용한다는 차이가 있음.
- 인터페이스를 통해 상호작용하므로 MVC가 가진 테스트 문제와 함께 모듈화/유연성 문제 역시 해결할 수 있음.
-  View에게 표시할 내용(Data)만 전달하며 어떻게 보여줄 지는 View가 담당.


본 프로젝트 패키지 구조  
![img_26.png](img_26.png)

## Jetpack Navigation [공식문서](https://developer.android.com/guide/navigation)
> Jetpack Navigation은 안드로이드 개발을 빠르게 도와주는 컴포넌트 라이브러리이다.
>  UI 를 통한 Navigation 편집이 가능하게 해주는 라이브러리로, 구글에서 권장하고 있는 네비게이션 중 하나이다.  
> 단순한 버튼 클릭부터 좀 더 복잡한 패턴(앱바, 탐색 창)에 이르기까지 여러 가지 탐색을 구현하도록 도와줍니다.  
> 탐색 구성요소는 기존의 원칙을 준수하여 일관적이고 예측 가능한 사용자 환경을 보장한다.

> **기본 구성 요소**  
> *탐색 그래프*: 모든 탐색 관련 정보가 하나의 중심 위치에 모여 있는 XML 리소스이다.  
> 여기에는 종착지(navigate의 마지막)이라고 부르는 앱 내의 모든 개별적 콘텐츠 영역과 사용자가 앱에서 갈 수 있는 모든 이용 가능한 경로가 포함된다.
>
> *NavHost*: 탐색 그래프에서 대상을 표시하는 빈 컨테이너이다. 대상 구성요소에는 프래그먼트 대상을 표시하는 기본 NavHost 구현인 NavHostFragment가 포함된다.
>
> *NavController*: NavHost에서 앱 탐색을 관리하는 객체이다. NavController는 사용자가 앱 내에서 이동할 때 NavHost에서 대상 콘텐츠의 전환을 주도한다.

>다음을 포함한 여러 가지 장점이 있습니다.
>
>1.프래그먼트 트랜잭션 처리.  
>2.기본적으로 '위로'와 '뒤로' 작업을 올바르게 처리.  
>3.애니메이션과 전환에 표준화된 리소스 제공.  
>4.딥 링크 구현 및 처리.  
>5.최소한의 추가 작업으로 탐색 UI 패턴(예: 탐색 창, 하단 탐색) 포함.  
>6.Safe Args - 대상 사이에서 데이터를 탐색하고 전달할 때 유형 안정성을 제공하는 그래프 플러그인입니다.  
>7.ViewModel 지원 - 탐색 그래프에 대한 ViewModel을 확인해 그래프 대상 사이에 UI 관련 데이터를 공유합니다.  
>8.또한 Android Studio의 Navigation Editor를 사용하여 탐색 그래프를 확인하고 편집할 수 있습니다.

> 기본 사용법
> 라이브러리이므로 의존성추가가 필요하다.
> ```groovy
>    // Jetpack Navigation
>    implementation 'androidx.navigation:navigation-fragment-ktx:2.5.3'
>    implementation 'androidx.navigation:navigation-ui-ktx:2.5.3'
>```
>
> res 경로에서 New > Directory 를 클릭하여 navigation 폴더를 생성하고,  
> navigation_main.xml(파일명은 원하는대로) 을 생성한다.
>
> navigation_main.xml 는 Navigation 탐색 그래프가 된다.  
> 탐색 그래프를 통해 앱 내에서 사용자가 이용 가능한 모든 경로가 표시된다.  
> xml 코드로 직접 조작 가능하고, UI 배치를 통해서도 조작이 가능하다.
> 여기서 프래그먼트 움직임을 조작한다.
>
> fragment 태그안에서
> 어떤 프래그먼트를 사용하는지 명시해준다.(name, layout)
> 레이아웃을 매핑해서 디자인에서 화면을 확인할 수 있도록 한다. (UI 배치)
> fragment 안에는 action, argument, deeplink 태그를 가질 수 있다.
> action 은 화살표로 화면간의 이동을 나타내고, argument 는 데이터 전달 시 사용한다.

```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/navigation_main"
    app:startDestination="@id/tracking_items_dest">
    <!-- 시작 fragment -->
    <fragment
        android:id="@+id/tracking_items_dest"
        android:name="com.fc.trackingdelivery.presentation.trackingitems.TrackingItemsFragment"
        android:label="택배 조회"
        tools:layout="@layout/fragment_tracking_items">
        <!-- 연결 동작 정의 -->
        <action
            android:id="@+id/to_add_tracking_item"
            app:destination="@id/add_tracking_item_dest" />

        <action
            android:id="@+id/to_tracking_history"
            app:destination="@id/tracking_history_dest" />
    </fragment>

    <fragment
        android:id="@+id/add_tracking_item_dest"
        android:name="com.fc.trackingdelivery.presentation.addtrackingitem.AddTrackingItemFragment"
        android:label="운송장 추가"
        tools:layout="@layout/fragment_add_tracking_item">
    </fragment>

    <fragment
        android:id="@+id/tracking_history_dest"
        android:name="com.fc.trackingdelivery.presentation.trackinghistory.TrackingHistoryFragment"
        android:label="배송 상세"
        tools:layout="@layout/fragment_tracking_history">
        <!-- 프래그먼트 전환 시 넘길 데이터 -->
        <argument
            android:name="item"
            app:argType="com.fc.trackingdelivery.data.entity.TrackingItem" />

        <argument
            android:name="information"
            app:argType="com.fc.trackingdelivery.data.entity.TrackingInformation" />
    </fragment>
</navigation>
```
```kotlin
// 프래그먼트 navigate
binding?.addTrackingItemButton?.setOnClickListener {
    findNavController().navigate(R.id.to_add_tracking_item)
}
// 프래그먼트 navigate
binding?.addTrackingItemFloatingActionButton?.setOnClickListener { _ ->
    findNavController().navigate(R.id.to_add_tracking_item)
}

// 리사이클러뷰의 아이템을 클릭하면 네비게이션 실행 (상세화면으로)
(binding?.recyclerView?.adapter as? TrackingItemsAdapter)?.onClickItemListener = { item, information ->
    findNavController()
        //safe-args에 의해 자동생성
        .navigate(TrackingItemsFragmentDirections.toTrackingHistory(item, information))
}
```


## Koin
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/ToDoList#mvp-architecture)
```kotlin
// 주입
val appModule = module {
    single { Dispatchers.IO }

    // Database
    single { AppDatabase.build(androidApplication()) }
    single { get<AppDatabase>().trackingItemDao() }
    single { get<AppDatabase>().shippingCompanyDao() }

    // Api
    single {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }
    single<SweetTrackerApi> {
        Retrofit.Builder().baseUrl(Url.SWEET_TRACKER_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }

    // Preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }

    // Repository
    single<TrackingItemRepository> { TrackingItemRepositoryImpl(get(), get(), get()) }
    single<ShippingCompanyRepository> { ShippingCompanyRepositoryImpl(get(), get(), get(), get()) }

//    single<TrackingItemRepository> { TrackingItemRepositoryStub() }


    // Fragments
    scope<TrackingItemsFragment> {
        scoped<TrackingItemsContract.Presenter> { TrackingItemsPresenter(getSource(), get()) }
    }
    scope<AddTrackingItemFragment> {
        scoped<AddTrackingItemsContract.Presenter> {
            AddTrackingItemPresenter(getSource(), get(), get())
        }
    }
    scope<TrackingHistoryFragment> {
        scoped<TrackingHistoryContract.Presenter> { (trackingItem: TrackingItem, trackingInformation: TrackingInformation) ->
            TrackingHistoryPresenter(getSource(), get(), trackingItem, trackingInformation)
        }
    }

    // Work
    single { AppWorkerFactory(get(), get()) }
}
```

## Room
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/basic/basic/Calculator#room-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

```@Embedded```  
여러 필드를 포함하고 있는 어떠한 객체를 POJO나 Entity를 통해 표현하고 싶을때가 있다.   
이런경우에는 @Embedded 애노테이션을 사용하여 테이블 내의 하위 필드로 분해 할 객체를 나타낼 수 있다.   
그 객체를 담고 있는 Entity가 직렬화가 되어있다면, Embedded된 Entity도 직렬화 해야한다.


```kotlin
@Parcelize
@Entity(primaryKeys = ["invoice", "code"])
data class TrackingItem(
    val invoice: String,
    @Embedded val company: ShippingCompany
) : Parcelable

@Parcelize
@Entity
data class ShippingCompany(
    @PrimaryKey
    @SerializedName("Code")
    val code: String,
    @SerializedName("Name")
    val name: String
): Parcelable
```

## Jetpack Work Manager [공식문서](https://developer.android.com/topic/libraries/architecture/workmanager?gclid=CjwKCAiAzKqdBhAnEiwAePEjkplEZpPiA-6U1tTNtTiUvJhfIxYmpxVq4-pvf21qWBRU9QhU4e1cZRoCcqYQAvD_BwE&gclsrc=aw.ds)
WorkManager는 비동기 작업을 쉽게 예약할 수 있는 API로 FirebaseJobDispatcher, GcmNetworkManager 및 Job Scheduler를 포함한 이전의 모든 Android 백그라운드 스케줄링 API를 대체할 수 있다.  
앱이 다시 시작되거나 시스템이 재부팅될 때 작업이 예약된 채로 남아 있으면 그 작업은 유지된다.  
대부분의 백그라운드 처리는 지속적인 작업을 통해 가장 잘 처리되므로 WorkManager는 백그라운드 처리에 권장하는 기본 API이다.

**지속적인 작업의 유형**

![출처: 공식문서](https://user-images.githubusercontent.com/79445881/209674454-2262f0e2-f6f9-4d59-9c2e-9172f31880bf.png)

WorkManager가 처리하는 지속적인 작업의 유형은 세 가지이다.

- 즉시: 즉시 시작하고 곧 완료해야 하는 작업입니다. 신속하게 처리될 수 있다.
- 장기 실행: 더 오래(10분 이상이 될 수 있음) 실행될 수 있는 작업.
- 지연 가능: 나중에 시작하며 주기적으로 실행될 수 있는 예약된 작업.


**특징**

WorkManager는 더 간단하고 일관성 있는 API를 제공할 뿐만 아니라 여러 가지 중요한 이점을 제공한다.

*작업 제약 조건*
- 작업 제약 조건을 사용하여 작업을 실행하는 데 최적인 조건을 선언적으로 정의한다. 예를 들어, 기기가 무제한 네트워크에 있을 때 또는 기기가 유휴 상태이거나 배터리가 충분할 때만 실행한다.

*강력한 예약 관리*
- WorkManager를 사용하면 가변 일정 예약 기간을 통해 한 번 또는 반복적으로 실행할 작업을 예약할 수 있다. 작업에 태그 및 이름을 지정하여 고유 작업 및 대체 가능한 작업을 예약하고 작업 그룹을 함께 모니터링하거나 취소할 수 있다.
- 예약된 작업은 내부적으로 관리되는 SQLite 데이터베이스에 저장되며 WorkManager에서 기기를 재부팅해도 작업이 유지되고 다시 예약되도록 보장한다.
- 또한 WorkManager는 절전 기능을 사용하고 권장사항(예: 잠자기 모드)을 준수하므로 배터리 소모를 걱정하지 않아도 된다.

*신속 처리 작업*
- WorkManager를 사용하여 백그라운드에서 즉시 실행할 작업을 예약할 수 있다. 사용자에게 중요하고 몇 분 내에 완료되는 작업에는 신속 처리 작업을 사용해야 한다.

*유연한 재시도 정책*
- 경우에 따라 작업이 실패하기도 한다. WorkManager는 구성 가능한 지수 백오프 정책을 비롯해 유연한 재시도 정책을 제공한다.

*작업 체이닝*
- 복잡한 관련 작업의 경우 직관적인 인터페이스를 사용하여 개별 작업을 함께 체이닝하면 순차적으로 실행할 작업과 동시에 실행할 작업을 제어할 수 있다.


**기본 가이드**
> jetpack 라이브러리의 일부이므로 의존성 설정이 필요하다.
>```groovy
> // Jetpack WorkManager
> implementation 'androidx.work:work-runtime-ktx:$work_version'
> ```
>
>
> 주요 클래스
> WorkManager를 이용해 작업을 등록하고 실행하려면 아래의 클래스를 이용한다.
>
> - Worker: 작업 내용을 가지는 추상 클래스
> - WorkRequest: 작업 의뢰 내용으로 이를 상속한 OneTimeWorkRequest, PeriodicWorkRequest 두 개의 클래스를 사용
> - Constraints: WorkRequst의 제약 조건 명시

예시 코드

해당 프로젝트에서 사용된 워커이다.  
doWork() 메서드는 WorkManager에 의해 제공되는 백그라운드 스레드에서 비동기적으로 실행된다.
휴대폰에 notification을 보내는 예약을 하는 Worker이다.
```kotlin
// Worker
class TrackingCheckWorker (
    // 코루틴워커 상속을 위한 기본 변수
    val context: Context,
    workerParams: WorkerParameters,
    // 주입해주어야 할것
    private val trackingItemRepository: TrackingItemRepository,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {

    // 실제 쓰레딩하는 로직(워커)
    override suspend fun doWork(): Result = withContext(dispatcher) {
        try {
            // 추적 정보를 가져오는데, 배송출발한 정보만 가져옴
            val startedTrackingItems = trackingItemRepository.getTrackingItemInformation()
                .filter { it.second.level == Level.START }

            // 비어있지 않으면 알림을 준비함
            if (startedTrackingItems.isNotEmpty()) {
                // 알림 채널 생성
                createNotificationChannelIfNeeded()

                // 배송 출발 목록 중, 대표로 첫번째 것을 가져옴
                val representativeItem = startedTrackingItems.first()

                // noti manager를 통해 휴대폰으로 알림을 보냄
                NotificationManagerCompat
                    .from(context)
                    .notify(
                        NOTIFICATION_ID,
                        createNotification(
                            "${representativeItem.second.itemName}(${representativeItem.first.company.name}) " +
                                    "외 ${startedTrackingItems.size - 1}건의 택배가 배송 출발하였습니다."
                        )
                    )
            }

            // 로직이 성공하면 (작업 성공) 성공을 반환
            Result.success()
        } catch (exception: Exception) {
            // 에러 시 실패를 반환
            Result.failure()
        }
    }

    // 채널 생성 함수
    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    // 알람 생성 함수 (인텐트-펜딩인텐트) 구현으로 알람 클릭 시 앱 실행
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(
        message: String?
    ): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)

        // builder 패턴을 통해 noti 생성
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_local_shipping_24)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        private const val CHANNEL_NAME = "Daily Tracking Updates"
        private const val CHANNEL_DESCRIPTION = "매일 배송 출발한 상품을 알려줍니다."
        private const val CHANNEL_ID = "Channel Id"
        private const val NOTIFICATION_ID = 101
    }
}
```


Worker가 정의되었다면 WorkManager를 통해 스케줄되어야 작업이 실행된다.  
WorkManager는 어떻게 작업을 스케줄할 지 다양한 유용성을 제공한다.  
일정한 기간 동안 주기적으로 실행되도록 예약하거나 한 번만 실행되도록 예약할 수 있다.
```kotlin
// WorkerRequest
// PeriodicWorkRequestBuilder - 주기로 반복되는 워커 request (ex) 1일, 변경가능)
// OneTimeWorkRequest - 단일 작업을 위한 워커 request
val dailyTrackingCheckRequest =
    PeriodicWorkRequestBuilder<TrackingCheckWorker>(1, TimeUnit.DAYS)
        // 딜레이 설정
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        // 실패 시 재시도 정책 (LINEAR - 몇 초뒤에)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .build()
```

마지막으로 enqueue() 메서드를 사용하여 WorkManager에게 WorkRequest를 제출한다.
```kotlin
// 워크매니저를 통해 워커 객체를 실행시켜 최종 워커를 만듦
        WorkManager.getInstance(this)
            // 주기적인 워크를 생성
            .enqueueUniquePeriodicWork(
                "DailyTrackingCheck",
                // 이미 워커가 존재하면 기존 것을 유지
                // 앱을 킬 때마다 initWorker가 실행되지만 새로 워커가 추가되지는 않는다. 이미 등록되어있기 때문에
                ExistingPeriodicWorkPolicy.KEEP,
                dailyTrackingCheckRequest
            )
```

이 프로젝트에서 해당 워커를 만들어 앱이 켜질 시 워커를 등록한다.
그리고 매일 한 번씩 정보를 받아 정보가 있으면, notification을 휴대폰에 보내준다.