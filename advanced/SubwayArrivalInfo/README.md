Subway Arrival Info
===


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
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/navigation_main"
    app:startDestination="@id/stations_dest">
    <!-- (위) 시작 프래그먼트 지정 -->
    
    <fragment
        android:id="@+id/stations_dest"
        android:name="com.fc.subwayarrivalinfo.presentation.stations.StationsFragment"
        tools:layout="@layout/fragment_stations">

        <!-- station_arrivals_dest로 화면이동함-->
        <action
            android:id="@+id/to_station_arrivals_action"
            app:destination="@id/station_arrivals_dest" />

    </fragment>

    <fragment
        android:id="@+id/station_arrivals_dest"
        android:name="com.fc.subwayarrivalinfo.presentation.stationarrivals.StationArrivalsFragment"
        tools:layout="@layout/fragment_station_arrivals">
        
        <!-- Station class를 받음 -->
        <argument
            android:name="station"
            app:argType="com.fc.subwayarrivalinfo.domain.Station"/>

    </fragment>

</navigation>
```
```kotlin
(binding?.recyclerView?.adapter as? StationsAdapter)?.apply {
    // 리사이클러뷰의 아이템을 클릭하면
    onItemClickListener = { station ->
        // StationsFragment에서 StationArrivals로 station 데이터를 보내며 화면전환
        // 여기서 StationsFragmentDirections.toStationArrivalsAction는 nav xml에의해 자동생성
        val action = StationsFragmentDirections.toStationArrivalsAction(station)
        findNavController().navigate(action)
    }
}
```

## Firebase Cloud Storage
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/UsedTrade#firebase-storage-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C)

> 본 프로젝트에서는 미리 구한 데이터 csv파일을 storage에 저장한 후 역 정보를 가져오고 실시간 지하철 정보 api를 통해
> 실시간 정보를 가져왔다.
>
```kotlin
// Firebase Storage 에서 data를 가져오는 api 함수를 정의한 클래스
class StationStorageApi(
    firebaseStorage: FirebaseStorage
) : StationApi {

    // firebase에서 station_data.csv 파일을 가져옴
    private val sheetReference = firebaseStorage.reference.child(STATION_DATA_FILE_NAME)

    // csv가 갱신된 최근 시간을 가져옴
    override suspend fun getStationDataUpdatedTimeMillis(): Long =
        sheetReference.metadata.await().updatedTimeMillis

    // csv 파일을 읽어 역, 노선 정보의 리스트로 바꿔 져옴
    override suspend fun getStationSubways(): List<Pair<StationEntity, SubwayEntity>> {
        val downloadSizeBytes = sheetReference.metadata.await().sizeBytes
        val byteArray = sheetReference.getBytes(downloadSizeBytes).await()

        return byteArray.decodeToString()
            .lines()
            .drop(1)
            .map { it.split(",") }
            .map { StationEntity(it[1]) to SubwayEntity(it[0].toInt()) }
    }

    companion object {
        private const val STATION_DATA_FILE_NAME = "station_data.csv"
    }
}
```

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
![img_25](https://user-images.githubusercontent.com/79445881/209439230-0a65396b-0638-45a9-8c21-c6f3cb3c551f.png)


## Koin
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/ToDoList#mvp-architecture)
```kotlin
// 프로젝트에 필요한 의존성 주입
val appModule = module {
    // IO 쓰레드
    single { Dispatchers.IO }

    // Database
    single { AppDatabase.build(androidApplication()) }
    single { get<AppDatabase>().stationDao() }

    // Preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }

    // Api - retrofit객체를 주입함
    single<StationApi> { StationStorageApi(Firebase.storage) }

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
    single<StationArrivalsApi> {
        Retrofit.Builder().baseUrl(Url.SEOUL_DATA_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }
    // Repository
    single<StationRepository> { StationRepositoryImpl(get(), get(), get(), get(), get()) }

    // Presentation
    // scope - 스코프 내에서 정의된 의존성은 이 내부(여기서는 stations fragment)에서만 사용, 공유할 수 있다
    scope<StationsFragment> {
        scoped<StationsContract.Presenter> { StationsPresenter(getSource(), get()) }
    }
    scope<StationArrivalsFragment> {
        scoped<StationArrivalsContract.Presenter> { StationArrivalsPresenter(getSource(), get(), get()) }
    }

}
```



## Room
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/basic/basic/Calculator#room-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

```@Transaction```
> db의 트랜잭션의 개념을 따른다.  
> Transaction 어노테이션을 활용하여 메서드의 질의들이 하나의 트랜잭션 안에서 실행되도록 한다.  
> 트랜잭션은 메서드의 Body 내부에서 Exception이 발생하면 반영되지않는다.
>
> @Transaction을 사용해서 쿼리를 실행시키면 좋을 경우
> > *결과가 매우 큰 경우* -  하나의 트랜잭션에서 질의함으로써 결과가 현재 담을 수 있는 커서보다 많더라도 커서윈도우 스왑간 변화에 영행을 주지 주지않는다.
> >
> > *결과값이 Relation으로 연결된 경우* - (Relation 어노테이션) 각 질의가 하나의 트랜잭션에서 실행된다면 데이터베이스의 일관성을 유시할 수 있다.
