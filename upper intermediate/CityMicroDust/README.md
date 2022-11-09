City MicroDust
===

## 공공 데이터 API with Retrofit
> 본 프로젝트에서는 미세먼지 데이터를 가져오기 위해,
> 본인 위치데이터를 통해 근접 미세먼지 측정소 정보를 API를 통해 가져와서 측정소에서 측정한 정보를
> 다시 가져오는 작업을 진행하였다.  
> 여기서 미세먼지 측정소 정보는 TM좌표를 요구하였기에   
> 카카오 API의 TM좌표 변환(GPS(위경도)->TM) API를 통해 TM좌표를 구하였다.
>
>
> API는 공공데이터 포탈- 한국환경공단 에어코리아의 대기오염정보, 측정소 정보를 활용하였다.
>
> ```kotlin
> interface AirKoreaApiService {
>     @GET("/B552584/MsrstnInfoInqireSvc/getMsrstnList?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}"
>        + "&returnType=json")
>     suspend fun getNearbyMonitoringStation(
>         @Query("tmX") tmX: Double,
>         @Query("tmY") tmY: Double
>     ): Response<MonitoringStationsResponse>
>
>     @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}"
>            + "&returnType=json"
>            + "&dataTerm=DAILY"
>            + "&ver=1.3")
>     suspend fun getRealtimeAirQualities(
>         @Query("stationName") stationName: String
>     ): Response<AirQualityResponse>
> }
>
> // 서버스 api 인터페이스
> interface KakaoLocalApiService {
>       @Headers("Authorization:KakaoAK ${BuildConfig.KAKAO_API_KEY}")
>       @GET("/v2/local/geo/transcoord.json?output_coord=TM")
>       suspend fun getTmCoordinates(
>           @Query("x") longitude:Double,
>           @Query("y") latitude: Double
>   ): Response<TmCoordinateResponse>
> }
> ```
>
> 위와 같이 서비스 인터페이스를 만든 후
> Retrofit 객체 생성을 통해 데이터를 가져온 후, 뷰에 바인딩 하였다.
> ```kotlin
> //ex
> Retrofit.Builder()
>   .baseUrl(Url.AIR_KOREA_BASE_URL)
>   .addConverterFactory(GsonConverterFactory.create())
>   // 로그 찍기 위해
>   .client(buildOkHttpClient())
>   .build()
>   // 원래 서비스 연결 시 서비스 파일을 명시해야하지만 Kotlin Extension에서 미리 정의된 함수로 바로 연결 가능
>   .create()
> ```


## 안드로이드 스튜디오 익스텐션
###  kotlin data class file from JSON
> 안드로이드 스튜디오의 익스텐션(확장 기능)으로 JSON 데이터를 입력하면  
> 자동으로 data class로 변환해준다.
> ![img_12](https://user-images.githubusercontent.com/79445881/200775497-b45d9d6c-2031-43e2-854b-ecc020a9873d.png)
> 다음과 같은 JSON데이터가 있다고 할 때
>
> ![img_15](https://user-images.githubusercontent.com/79445881/200775549-2b9992db-73b7-4a67-8a80-e41eb703e940.png)
> ![img_16](https://user-images.githubusercontent.com/79445881/200775596-90505b26-17a4-4712-86ab-0a0ed115ee52.png)
> ![img_17](https://user-images.githubusercontent.com/79445881/200775602-7331c33b-ceb6-4975-8dd4-c62e3b8c104e.png)
>
> 위와 같이 JSON을 파싱하여 data class를 자동생성 해준다.
>
>![img_13](https://user-images.githubusercontent.com/79445881/200775616-9f062c80-5211-4f9b-a30f-a5a78fc9de1d.png)
>![img_14](https://user-images.githubusercontent.com/79445881/200775626-c0b1aa6e-8a9d-436d-81b9-3ba0d5042086.png)
> 위와 같이 상세 설정도 가능하다.


## SwipeRefreshLayout
> ```SwipeRefreshLayout```은 사용자가 수동으로 뷰를 업데이트 할 수 있게 해주는 레이아웃이다.  
> Swipe 동작을 통해 뷰를 새로고침 할 수 있다.

![KakaoTalk_20221109_163410391](https://user-images.githubusercontent.com/79445881/200767495-7ae97dda-6448-40ab-9740-dfad45e1cc5f.gif)

> 사용법
>
> 먼저, 앱 수준 gradle에 의존성을 추가해주어야 한다.  
> ```implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'```
>
> 의존성을 추가해준 후 사용할 뷰 xml 파일로 들어가서
>
> refresh를 적용하고자 하는 뷰 계층에
> ```xml
> <androidx.swiperefreshlayout.widget.SwipeRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
>   xmlns:app="http://schemas.android.com/apk/res-auto"
>   xmlns:tools="http://schemas.android.com/tools"
>   android:id="@+id/refresh"
>   android:layout_width="match_parent"
>   android:layout_height="match_parent"
>   android:fitsSystemWindows="true">
> 
>   <!-- 다른 뷰 삽입 -->
> 
> </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
>  ```
> 위와 같이 정의하여 사용한다.
>
>
> 제어
> ```setOnRefreshListener```를 통해 refresh시 할 동작을 정의한다.
> ```kotlin
> binding?.refresh?.setOnRefreshListener {
>     fetchAirQualityData()
> }
>```



## Android Widget [공식문서](https://developer.android.com/guide/topics/appwidgets?hl=ko)
> 휴대폰에 위젯을 만드는 작업이다.
>
> 먼저, 위젯이 될 뷰를 구성한다.
> ~~뷰는 앱 레이아웃 구성하듯이 구성하면 된다.~~
> 
> (2022-11-09 오후 7:02 수정)  
> 레이아웃에 익숙한 경우 간단히 앱 위젯 레이아웃을 만들 수 있다.  
> 하지만 앱 위젯 레이아웃은 모든 종류의 레이아웃 또는 뷰 위젯을 지원하지는 않는 ```RemoteViews```을 기반한다.  
> 
> **Remote Views** 지원 레이아웃
> ```FrameLayout```, ```LinearLayout```, ```RelativeLayout```, ```GridLayout```  
> 
>
> 그리고 xml 디렉토리에 ```appwidget-provider```속성으로 위젯의 설정 정보를 정의한다.
> ```xml
> <appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
>   android:initialLayout="@layout/widget_simple"
>   android:minWidth="110dp"
>   android:minHeight="50dp"
>   android:updatePeriodMillis="3600000"
>   android:resizeMode="none"
>   android:widgetCategory="home_screen"
> />
> 
> ```
>
> ```android:initialLayout```: 위젯 초기 레이아웃 설정  
> ```android:minWidth android:minHeight```: 최소 높이, 폭 설정  
> ```android:updatePeriodMillis```: 위젯 업데이트 주기 설정  
> ```android:resizeMode```: 위젯 크기조정을 막을 지 설정
> ```android:widgetCategory```: 위젯의 종류 설정
>
>
> 그 후, ```AppWidgetProvider()```를 상속한 브로드캐스트 리시버를 만들어 설정해준다.
>
> ```kotlin
> // 이 클래스는 브로드 캐스트 리시버이기 떄문에 시스템에의해 언제든지 취소 당할 수 있다.
> class SimpleAirQualityWidgetProvider : AppWidgetProvider() {
> // 그래서 업데이트 될 때, 서비스를 시작하여 강제 취소를 막아야한다.
>   override fun onUpdate(
>   context: Context?,
>   appWidgetManager: AppWidgetManager?,
>   appWidgetIds: IntArray?
>   ) {
>           super.onUpdate(context, appWidgetManager, appWidgetIds)
>
>             ContextCompat.startForegroundService(
>                context!!,
>                Intent(context, UpdateWidgetService::class.java)
>            )
>         }
>
>     // 위젯에서 정보를 보여줄 서비스 클래스 (lifeCycle에 따라 정의)
>     class UpdateWidgetService : LifecycleService() {
>           // 서비스에서 실행할 작업 정의
>       }
> }
> ```
>
>
> 브로드캐스트 리시버이기 때문에 manifest에 정의해준다.
> ```xml
> <receiver android:name=".appwidget.SimpleAirQualityWidgetProvider"
>      android:exported="true">
>       <intent-filter>
>            <action android:name="android.appwidget.action.APPWIDGET_UPDATE"/>
>       </intent-filter>
>        <meta-data android:name="android.appwidget.provider"
>              android:resource="@xml/widget_simple_info"/>
>  </receiver>
> ```
>
> 위젯의 브로드캐스트 리시버, 서비스는 추가적인 학습이 필요할 것 같다.
> 일단 기본적인 사용법만 작성하였다.