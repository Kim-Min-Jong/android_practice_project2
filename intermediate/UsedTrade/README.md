중고거래앱
===

## **View Binding** 사용하기
[복습- activity viewBinding](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/BookReview#viewbinding-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)
+ fragment viewBinding
> 프래그먼트 뷰바인딩 또한, 액티비티 뷰바인딩과 하는 방법이 같다.  
> ```fragment_main.xml = FragmentMainBinding``` 식으로 치환하여 코드로 사용하면된다.
>
> 예시 코드
> ```kotlin
> binding = FragmentMyPageBinding.inflate(layoutinflater)
>```
>
> > *추가*  
> > .bind(view) 메소드
> > ```kotlin 
> > binding = FragmentMyPageBinding.bind(view) 
> > ```
> >위와 같이 사용하며, 프래그먼트의 요소들에 ```findViewById```를 적용하여 전체적인 바인딩을 한다.

## **Fragment** 사용하기
> 다양한 크기의 화면을 가지는 기기가 늘어남에 따라 한 화면에 여러 개의 화면 요소를 원하는 수요가 늘어가고 있다. 대표적으로 화면의 크기가 큰 태블릿 PC와 같이 화면의 크기가 큼에 따라 복잡한 레이아웃 구성과 뷰 위젯 배치들로 인해 기존의 Activity를 통한 레이아웃 구성만으로는 구현하기 버거운 면이 있었다.  
> 이를 해결하기위해 나온 것이 안드로이다 3.0부터 추가된 개념인 fragment이다.  
> 프래그먼트는 액티비티 내에서 화면 UI의 일부를 나타낸다.  
> 여러 개의 프래그먼트를 조합하여 액티비티가 출력하는 한 화면의 UI를 표현할 수 있으며, 하나의 프래그먼트를 다른 여러 액티비티에 **재사용**할 수 있다
> 프래그먼트는 액티비티와 같이 독립적이로 실행되기 떄문에, 자체적인 생명주기를 갖고있다.
> 또, 액티비티 실행중에도 다른 fragment로 교체가 가능하다.  
> 기본적으로 한 개의 액티비티에 들어가는 화면 요소를 Fragment 단위로 나누어 관리하기 때문에 레이아웃을 분리 관리할 수 있고, 액티비티의 화면 구성을 위한 레이아웃의 복잡도도 줄일 수 있다.
>![프래그먼트가 정의한 두 가지 UI 모듈이 태블릿 디자인에서는 하나의 액티비티로 조합될 수 있는 반면, 핸드셋 디자인에서는 분리될 수 있다는 것을 나타낸 예시.](https://developer.android.com/static/images/fundamentals/fragments.png)


> **프래그먼트 사용법**  
> 우선, 프래그먼트를 붙일 액티비티를 정한다.
> ```MainActivity```를 부모 컴포넌트라고 하고, 여기에 붙일 프래그먼트를 ```A Fragment```,```B Fragment```라고 하자
>
> 먼저, 액티비티 xml에 프래그먼트를 보여줄 레이아웃을 선언한다.
>  ```xml
> <FrameLayout
>   android:id="@+id/fragmentContainer"/>
> ```
> 위와 같이 특정 레이아웃에다 붙혀도 되고, ```<fragment/>``` 라는 태그도 있는데, 이것을 사용해도 된다.
> ```<fragment android:name="B Fragnent" ... />``` 식으로 파일을 직접 넣어 사용할 수 있다.  
> 그리고 ```Fragment()```를 상속하는 클래스를 만들어 프래그먼트 생명주기에 맞게, UI초기화 , 기타로직등을 작성하면 된다.
> (액티비티 생명주기에 맞취 코딩하는 것과 비슷하다.)
> ```kotlin
> // ex)
> class HomeFragment : Fragment(R.layout.fragment_home) {}
>  ```

+ ###**fragment lifecycle**
> 위에서 이야기 했듯이, 프래그먼트도 독립적으로 동작하기 때문에 자체적인 생명주기를 가진다.
> 프래그먼트의 생명주기를 알아보자.
> ![프래그먼트 생명주기-출처:공식홈페이지](https://developer.android.com/static/images/guide/fragments/fragment-view-lifecycle.png)
> ```onAttach()```: 프래그먼트가 액티비티와 연결되어 있던 경우 호출된다. 여기서 액티비티가 전달된다.
>
> ```onCreate()```: 프래그먼트를 생성할 때 호출된다. 프래그먼트가 pause, stop 후 재개되었을 때, 유지해야할 것들을 초기화 해줘야 한다.
>
> ```onCreateView()```: 시스템이 사용자 인터페이스를 처음으로 그릴 때 호출된다. 래그먼트에 맞는 UI를 그리려면 메서드에서 View를 반환해야 한다. 이 메서드는 프래그먼트 레이아웃의 루트(시작점)이다. 프래그먼트가 UI를 제공하지 않는 경우 null을 반환하면 된다.
>> *주의*  
>> onCreateView()에서 view가 초기화중이기 때문에 충돌이 일어날수 있다. 여기서 레이아웃을 inflate하지만 findViewById등을 사용하여 초기화는 하지 말아야한다.  
>> 몇가지 뷰들은 초기화가 되어있지 않을 수 있기 때문이다. 그래서 view가 완전히 생성됐을 때 호출되는 ```onViewCreated()```에서 findViewById등을 통해서 초기화 해주어야한다.
>
> ```onViewCreated()```: 프래그먼트의 뷰가 완전히 생성된 후 호출된다. 여기서 view 컴포넌트들을 초기화해주는 로직을 작성한다.  
> ex) viewBinding, recyclerview-viewpager adapter 초기화 등..
>
> ```onViewStateRestored()```: 프래그먼트에 속하는 컴포넌트들의 상태값을 모두 읽어왔을 때 호출된다. ex) 버튼이 눌렸는지..
>
> ```onStart(), onResume(), onPause(), onStop()```: 이 메소드들은 액티비티 생명주기에 있는 메소드들과 같다. [참고](https://github.com/Kim-Min-Jong/android_practice_project2/tree/basic/basic/Digital_Photoframe#android-lifecycle-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B8%B0)
>
> ```onSavedInstanceState()```: 프래그먼트의 상태값이 저장된 후 호출되는 메소드이다. onStop()이 호출 된 후 프래그먼트의 상태가 저장된다.
>
> ```onDestroyView()```: 프래그먼트와 연결된 view layer가 제거 되는 중일 때 호출된다.
>
> ```onDetach()```: 프래그먼트가 액티비티와 연결이 끊어지는 중에 호출된다.
>
## **BottomNavigationView** 사용하기
> 안드로이드에서 하단 바를 쉽게 만들기 위한 머터리얼 컴포넌트이다.
> 액티비티에 놓고 프래그먼트를 전환하기 위해서 주로 사용한다.
>
> ```xml
> <com.google.android.material.bottomnavigation.BottomNavigationView
>   android:id="@+id/bottomNavigationView"
>   android:layout_width="0dp"
>   android:layout_height="wrap_content" 
>   app:menu="@menu/bottom_navigation_menu"
>   app:itemIconTint="@drawable/selector_menu_color"
>   app:itemTextColor="@color/black"
>   app:itemRippleColor="@null"
> />
> ```
> **menu 속성**  
> 하단 바에 들어갈 메뉴들을 정의하는 레이아웃 파일이다. 메뉴가 있어야 사용할 수 있다.
>
> ```kotlin
> binding?.bottomNavigationView?.setOnItemSelectedListener{
>    when(it.itemId){
>        R.id.home-> replaceFragment(homeFragment)
>        R.id.chatList-> replaceFragment(chatListFragment)
>        R.id.myPage-> replaceFragment(myPageFragment)
>    }
>    true
> }
> 
>     //프래그먼트 전환
> private fun replaceFragment(fragment: Fragment) {
>     supportFragmentManager.beginTransaction().apply {
>        replace(R.id.fragmentContainer, fragment)
>        commit()
>     }
> }
> ```
> bottomNavigationView의 ```.setOnItemSelectedListener()``` 메소드를 통해 메뉴 선택시 아이디 값을 기반으로 프래그먼트를 전환한다.




## **FloatingActionButton** 사용하기
> FloatingActionButton은 앱 뷰에 떠있는 작은 버튼을 만들어 기본 작업들을 트리거 할 수 있게 해준다.  
>![](https://developer.android.com/static/training/material/images/fab.png?hl=ko)
> 머티리얼 디자인 가이드라인에 따라 앱에 플로팅 작업 버튼을 디자인하는 방법을 자세히 알아보려면 [버튼: 플로팅 작업 버튼](https://material.io/design/components/buttons-floating-action-button.html)
> 도 참고하길 바란다.
>
> *사용법*  
> 먼저 레이아웃을 선언한다.
> ```xml
>    <com.google.android.material.floatingactionbutton.FloatingActionButton
>        android:id="@+id/addFloatingBtn"
>        android:layout_width="wrap_content"
>        android:layout_height="wrap_content"
>        android:layout_margin="16dp"
>        android:backgroundTint="@color/orange"
>        android:src="@drawable/ic_baseline_add_24"
>        app:layout_constraintBottom_toBottomOf="parent"
>        app:layout_constraintEnd_toEndOf="parent"
>        app:tint="@color/white" />
> ```
> **주의**
>> 머터리얼 UI의 일부여서 color속성으로 버튼의 색을 바꾸지 못한다.  
>> backgroundTint 속성을 통해 버튼 색을 바꿀 수 있다. (다른 머터리얼 디자인에도 적용가능)
>
> *동작*
> 일반적인 버튼과 같이 ```setOnClickListener```를 통해 버튼 클릭시 행동을 정의한다.
> ```kotlin
> binding?.addFloatingBtn?.setOnClickListener{
>   //할 일 정의 ...
> }
> ```

## **Firebase Storage** 사용하기 [공식문서](https://firebase.google.com/docs/storage?hl=ko)
> Firebase Cloud Storage는 사진, 동영상 등의 사용자 제작 콘텐츠를 저장하고 제공해야 하는 앱 개발자를 위해 만들어졌다.  
> AWS S3와 비슷하다고도 볼 수 있을 것 같다.

> **기본세팅**
> firebase 콘솔에서 Storage 메뉴에 들어가 시작하기를 누르고 저장될 서버위치를 지정한다.
> 그 후, 코드를 통해 사용한다
>
> **예시 코드**
> ```kotlin
>     private val storage: FirebaseStorage by lazy {
>        Firebase.storage
>    }
>    storage.reference.child("article/photo").child(fileName)
>        .putFile(photoUri).addOnCompleteListener{
>            if(it.isSuccessful){
>                // 스토리지에 성공적으로 넣었을 시 다시 그 넣어진 url을 가져와 successhandler를 실행
>                storage.reference.child("article/photo").child(fileName).downloadUrl
>                    .addOnSuccessListener { uri ->
>                        // 성공하면 핸들러로 가서 uri를 포함한 믈픔을 realtime db에 저장함
>                        successHandler(uri.toString())
>                    }.addOnFailureListener {
>                        // 실패시 uri없이 db에 물품정보 저장
>                        errorHandler()
>                    }
>            }  else{
>                errorHandler()
>            }
>        }
> ``` 
> realtime database와 비슷하게 디렉토리 트리를 만들며 저장이 되기 때문에 ```child()```메소드를 통해 하위 디렉토리를 만들 수 있다.
> 그리고 ```putFile(uri)``` 메소드를 통해 firebase storage에 이미지 등을 저장 할 수 있다.  
>  ```storage.reference.downloadUrl```을 통해 firebase storage에 저장된 이미지의 url을 가져와서 사용할 수 있다.( ex) url을 가져와 이미지뷰에 바인딩)

> **주의**  
> 이미지 저장시 Rules 떄문에 저장이 안되는 경우가 있음  
> storage의 rules에서 규칙을 바꿀 수 있음  
> ```allow read, write: if true``` : 누구나 읽고 쓸수 있음(보안상 문제 발생가능, 추천 x)  
> ```allow write: if request.auth != null``` : 인증이 된 사용자는 쓰기가능  
> 등 Rule을 정해서 보안 상 생길 수 있는 문제를 해결해야 한다.
> > *본 프로젝트에선 테스트 모드 규칙을 적용하였다.*




## **Firebase Realtime Database**
이전 틴더 앱과 동일한 사용법을 적용하였다.  
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/Tinder#firebase-realtime-database-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C)

## **Firebase Authentication** 사용하기
이전 틴더 앱과 동일한 사용법을 적용하였다.  
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/Tinder#firebase-authentication-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C)


## **RecyclerView** 사용하기
>android jetpack 구성요소 중 하나이다.
>대량의 데이터를 동적, 효율적으로 보여주기위한 뷰 레이아웃이다.
>View의 item들을 adapter를 통해 binding해준다. listView는 item을 만들때 마다 새로 만들지만, recyclerView는 이전 item이 사용하던 공간을 재활용(그 자리에 binding)하여 사용한다.  
[기본사용법(kotlin)-출처:codechacha](https://codechacha.com/ko/android-recyclerview/)

## 추가
Advanced - Camera app 의 카메라 기능을 추가하여 갤러리 뿐만 아니라 카메라로 직접 사진을 찍어
글을 작성할 수 있는 기능을 추가하였다.

[카메라](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/Camera#camera-app)

### registerForActivityResult
deprecated 된 starActivityForResult, onActivityResult를 대체하기 위해서 사용  
(본 프로젝트에서는 카메라로 사진을 찍은 후 다음 액티비티로 넘어가 선택할 사진을 고를 수 있도록 하였는데  
이때, 글쓰기 액티비티로 사진 데이터를 받아주어야 하기때문에 ActivityResult를 사용하였다.)

기존에는 ```starActivityForResult```로 액티비티를 실행하고, ```onActivityResult```에서 받아온 데이터를 처리하였는데
다음과 같은 이유로 deprecated 되었다.  
```결과를 얻는 Activity를 실행하는 로직을 사용할 때, 메모리 부족으로 인해 프로세스와 Activity가 사라질 수 있다. (특히 카메라 같은 메모리를 많이 사용하는 작업은 소멸 확률이 굉장히 높다.)```

그래서 ```registerForActivityResult```라는 대체제가 나와 사용할 수 있다.


``` kotlin
// 기존의 onActivityResult 역할 
val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
       // 액티비티가 정상적으로 돌아왔을 경우 콜백
            
       if (it.resultCode == Activity.RESULT_OK) {
       // 할 동작을 정의
       // 기존의 onActivityResult 메소드의 작업을 여기에다 정의해 주면됨
       // ...
       }
}

// 기존의 startActivityForResult의 역할 (requestCode를 보낼 필요가 없어졌음)
val intent = Intent(this, 넘어갈 액티비티)
launcher.launch(intent)
```

## 중고거래앱

Firebase Authentication 기능을 사용하여 로그인 회원가입 기능을 구현할 수 있음.

회원 기반으로 중고거래 아이템을 등록할 수 있음.

아이템 등록 시 사진 업로드를 위해 Firebase Storage 를 사용할 수 있음.

회원 기반으로 채팅 화면을 구현할 수 있음.

Fragment 를 사용하여 하단 탭 화면 구조를 구현할 수 있음.

FloatingActionButton 을 사용하기