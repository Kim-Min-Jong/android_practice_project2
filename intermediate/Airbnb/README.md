Airbnb app
===

## **Naver Map API** 사용하기 [공식문서](https://guide.ncloud-docs.com/docs/ko/naveropenapiv3-maps-overview)
> naver cloud platform에서 제공하는 map api이다.  
> naver cloud platform console에서 앱 등록 후 사용 가능하다.
>
>  먼저, implement를 해야한다.
>  app수준 gradle에 ```implementation 'com.naver.maps:map-sdk:3.15.0'```를 추가하고
>
> setting.gradle에 maven 저장소를 추가한다.(기존에는 프로젝트 수준의 gradle의 allprojects의 repository에서 불러왔지만,
> 안드로이드 스튜디오 arctic fox 버전 이후로는 setting.gradle에 추가해야한다.)
>
> ```groovy
> dependencyResolutionManagement {
>      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
>        repositories {
>           google()
>           mavenCentral()
>        //   
>        maven {
>                url 'https://naver.jfrog.io/artifactory/maven/'
>            }
>         }
>       //
>    }
> ```
>
> 사용 예제
> ```kotlin
> // 맵뷰에 생명주기 연결결
> binding?.mapView?.onCreate(savedInstanceState)
>
> // 맵 가져오기
> // 메인 액티비티에 onMapReadyCallback을 상속시켜 액티비티 자체를 구현체로 만들어 콜백으로 사용한다.
> // 람다로 쓰는 방식도 있긴한데 오버라이드 할것이 많아서 이렇게 사용한다.
> binding?.mapView?.getMapAsync(this)
>
> // OnMapReadyCallback의 실 구현체 (지도 조작)
> override fun onMapReady(map: NaverMap) {
> naverMap = map
>
> // 최대 최소 줌 정도 설정
> //        naverMap.maxZoom = 18.0
> //        naverMap.minZoom = 10.0
>
> // 초기 설정 지역 (강남역)
> val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
> naverMap.moveCamera(cameraUpdate)
>
> //현 위치 버튼 생성 및 현 위치 이동(권한 필요)
> val uiSetting = naverMap.uiSettings
> uiSetting.isLocationButtonEnabled = false
> // 버튼위치 이동 및 바인딩딩
> binding?.currentLocation?.map = naverMap
>
> //location service 등록  (권한 생성)
> locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
> naverMap.locationSource = locationSource
>
> // 마커 찍기
> //        val marker = Marker()
> //        marker.position = LatLng(37.123123,127.123123)
>//        marker.map = naverMap
>
>```
> 그리고 맵에 있한 메모리 누수 방지를 위해 안드로이드 모든 생명주기와 맵 생명주기를 맞춰준다
> ```kotlin
> // .... onCreate, onStart, onResume...
>    override fun onStop() {
>        super.onStop()
>        binding?.mapView?.onStop()
>    }
>
>    override fun onLowMemory() {
>        super.onLowMemory()
>        binding?.mapView?.onLowMemory()
>    }
>
>    override fun onDestroy() {
>        super.onDestroy()
>        binding = null
>        binding?.mapView?.onDestroy()
>    }
>```


## **FrameLayout** 알아보기 [공식문서](https://developer.android.com/reference/android/widget/FrameLayout?hl=en)
> FrameLayout은 여러개의 뷰 위젯들을 중첩하고, 그 중 하나를 맨 위에 표시 할 때
> 사용하는 레이아웃이다. 이렇게 여러뷰를 겹치게 배치할수 있는 프레임 레이아웃의 특징이,
> 뷰 위젯을 겹치지 않고 순서대로 배치하는 LinearLayout과 가장 큰 차이점이다.
> FrameLayout의 단어에서 볼 수 있듯이 액자처럼 뷰들을 마음대로 빼고,넣으며
> 경우에 따라 보여주고 싶은 화면을 바꿔가며 보여줄 수 있다.
> ![출처-맛있는프로그래머의일상](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FeK05ub%2FbtquVgGWGOp%2F9gs00Nm8qCntijokmhU7zk%2Fimg.png)
>
> 사용은 xml의 layout을 사용하듯이 사용하면 된다.
> 다양한 속성은 공식문서를 참고하면 좋을 것 같다.

## **CoordinatorLayout** 사용하기 + **BottomSheetBehavior** 사용하기
> **CoordinatorLayout**은 FrameLayout을 기반으로 둔 레이아웃으로 주요 2가지 기능이 있다.
> 1. 최상위 Decor뷰로써의 사용
> 2. 자식 뷰들간의 인터렉션을 위한 컨테이너로써의 사용
>
> *CoordinatorLayout*의 ```Behaviors``` 속성 지정을 통해 부모 뷰 내에서 다양한 상호작용을 적용
> 할 수 있다.
> 보통 *CoordinatorLayout*은 스크롤 이벤트에따라 앱바의 변화를 줄 때 사용한다.
> 이 프로젝트에서는 하단에 더 보기바를 만들고 위로 스크롤하여 새로운 뷰를 보여주기위해 사용된다.
> (BottomSheetBehavior와 함께 사용함)
>
>
> **BottomSheetBehavior**  
> ```BottomSheetBehavior```는 ```CoordinatorLayout```에서 자식뷰에 대한 플러그인 중 하나이다.  
> 자식 뷰의 ```app:layout_behavior```에서 설정해주면 하단에서 펼쳐지는 방식으로 자식 뷰가 동작하게 된다.  
> ```BottomSheetDialog``` 나 ```BottomSheetDialogFragment``` 도 같은 방식으로 동작한다.
>
> 사용법
> > 이 프로젝트에서는 두개의 레이아웃을 통해 구현하였다.
> > ```xml
> > <!-- activity_main.xml -->
> > <androidx.coordinatorlayout.widget.CoordinatorLayout>
> > 
> >    <!-- ... 기타 뷰 정의 -->    
> > 
> >    <include
> >           android:id="@+id/included"
> >           layout="@layout/bottom_sheet"/>
> > </androidx.coordinatorlayout.widget.CoordinatorLayout>
> > ```
> > 부모가 될 뷰에서는 자식뷰를 include해서 가져온다.
> >
> >
> > ```xml
> > <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
> >     android:layout_width="match_parent"
> >     android:layout_height="match_parent"
> >     android:background="@drawable/top_radius_white_background"
> >     app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior"
> >     app:behavior_peekHeight="100dp"
> >     xmlns:app="http://schemas.android.com/apk/res-auto">
> > 
> >   <!-- ... 기타 뷰 정의 --> 
> > 
> > </androidx.constraintlayout.widget.ConstraintLayout>
> > ```
> > 자식 뷰에서는 ```app:layout_behavior```속성을 ```com.google.android.material.bottomsheet.BottomSheetBehavior```으로 지정하여  
> >```BottomSheetBehavior```를 연결시킨다.  
> >```app:behavior_peekHeight```속성은 자식뷰가 부모 뷰에 얼만큼의 높이만큼을 보이게 할지 정하는 속성이다.
>
> 이렇게 레이아웃 설정을 한 후, 각 레이아웃에서 필요에 맞게 뷰 위젯들을 넣어 사용하면 된다.
>
> 동작
> ![KakaoTalk_20221011_210020531](https://user-images.githubusercontent.com/79445881/195089134-1ea09afe-13a2-41c5-a4ab-d89f96577ca9.gif)



## **ViewPager2** 사용하기
> [복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/TodayQuotes#view-pager2)  
> **사용 시 주의점**  
> 내부에 들어가는 레이아웃이 고정값이 아니라 match_parent를 주어야 정상동작한다.  
> viewPager의 아이템에 이미지 하나를 보여주는 경우가 있는데, 이때 고정값을 사용하면   
> 기기별로 실제 가지는 높이가 다르게 나타날 수 있기 때문이다.


## **Retrofit2** 사용하기
[복습 기본 사용법](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/BookReview#retrofit2-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-api-%ED%98%B8%EC%B6%9C)

>**Mocky를 통해 Mock API와 함께 사용하기**  
> 우선, mock api란 말 그대로 가짜 API 서버이다. 클라이언트 요청에 실제 서버처럼 동작하기보다는 미리 저장된 데이터를 단순하게 돌려주는 형태이다. 다시 말해 이는 가짜 서버를 사용해 실제 서버와 통신하는 것처럼 만들 수 있다.
> 이 프로젝트에선 실제 서버의 데이터를 불러오는 것이 아닌 임의의 데이터를 만들어 mock server를 만들고 통신하였다.
> Mocky란 Mock API 제작 서비스이다. 별도의 로그인 없이 빠르게 서비스를 이용할 수 있다.
>
> 사용법  
> 먼저, 임의로 사용할 json 파일을 만든다.  
> 그리고, [Mocky 사이트](https://designer.mocky.io/) 로 들어가 우측 상단에 NewMock을 클릭한다.
> ![img_2.png](img_2.png)
>
> 그리고 HTTP Response Body에 사용할 json을 붙여 넣는다.
> ![img_3.png](img_3.png)
>
> 그 후, 하단에 GENERATE MY HTTP RESPONSE 를 클릭하면 mock server가 만들어진다.
> ![img_4.png](img_4.png)
> ![img_5.png](img_5.png)
> 만들어진 url로 retrofit 통신을 하면 된다.



## **Glide** 사용하기
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/BookReview#glide-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0%EC%9D%B4%EB%AF%B8%EC%A7%80-%EB%A1%9C%EB%94%A9-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC)


> *glide 이미지 조작하기*
> glide는 이미지 로딩과 더불어 이미지를 조작할수 있는 ```transform()```이란 메소드를 제공한다.
> 이 메소드를 통해 이미지를 변경, 조작할 수 있다.
> BitmapTransfomation을 사용한다.
> 기본 내장되어있는 tranform은 ```CenterCrop```,```FitCenter```,```CircleCrop``` 등이 있다.
>
> ```CenterCrop``` :  실제 이미지가 이미지뷰의 사이즈보다 클 때, 이미지뷰의 크기에 맞춰 이미지 중간부분을 잘라서 스케일링한다.  
> ```CircleCrop``` : 실제 이미지가 이미지뷰의 사이즈보다 클 때, 이미지뷰의 크기에 맞춰 이미지 중간부분을 잘라서 스케일링하고 원형으로 표시한다.  
> ```FitCenter``` : 실제 이미지가 이미지뷰의 사이즈와 다를 때, 이미지와 이미지뷰의 중간을 맞춰서 이미지 크기를 스케일링한다.
>
> ```kotlin
> //ex)
> Glide.with(binding.thumbnailImageView.context)
>      .load(houseModel.imageUrl)
>      .transform(CenterCrop())
>      .into(binding.thumbnailImageView)
> ```



>[transformations 공식문서](https://bumptech.github.io/glide/doc/transformations.html)


## 에어비엔비

Naver Map API 를 이용해서 지도를 띄우고 활용할 수 있음.

Mock API 에서 예약가능 숙소 목록을 받아와서 지도에 표시할 수 있음.

BottomSheetView 를 활용해서 예약 가능 숙소 목록을 인터렉션하게 표시할 수 있음.

ViewPager2 를 활용해서 현재 보고있는 숙소를 표시할 수 있음.

숙소버튼을 눌러 현재 보고 있는 숙소를 앱 외부로 공유할 수 있음.