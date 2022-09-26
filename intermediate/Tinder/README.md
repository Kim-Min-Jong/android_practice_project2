Tinder
===

## Firebase Authentication 사용하기 [공식문서](https://firebase.google.com/docs/auth?hl=ko)
> Firebase 인증은 앱에서 사용자 인증 시 필요한 백엔드 서비스와 사용하기 쉬운 SDK, 기성 UI 라이브러리를 제공한다.   
> 비밀번호, 전화번호, 인기 제휴 ID 공급업체(예: Google, Facebook, Twitter 등)를 통해 인증이 지원된다.  
> 즉, 로그인등을 통한 신원인증을 할 수 있다.  
> 여러 방법이 있는데, 이메일 로그인과 페이스북 로그인을 알아보고자 한다.
>
> *기본 세팅*  
>  파이어 베이스에서 Authentication 항목으로 가서 시작하기를 눌러 시작을 한다.  
>  그 후, sign-in method로 들어가 로그인 제공업체(이메일, twitter, facebook..) 등을 추가한다.  
>  각 업체마다 앱 정보 입력등 기타 사항을 요구 할 수 있으니 안내에 따라 등록과정을 거치면 된다.  
>  제공업체가 추가가 되면 이제 코드 상에서 구현을 한다.


> **구현 공통 사항**  
> Auth 인스턴스를 가져와서 사용해야한다.
> ```kotlin
> private lateinit var auth: FirebaseAuth
> auth = Firebase.auth
>```

+ **email login**
> 이메일 로그인은 @ .com등의 형식인 이메일 방식의 인증을 말한다.  
> 이메일 로그인 같은 경우는 기타 업체에 이미 있는 계정으로 로그인하는 것과 달리 이메일 외의 정보가 없으므로 회원 등록 과정을 거쳐야한다.
> ```kotlin
>  auth.createUserWithEmailAndPassword(email, pwd)
>        .addOnCompleteListener(this) {
>              if(it.isSuccessful){
>                    Toast.makeText(this, "회원가입에 성공했습니다. 로그인해주세요", Toast.LENGTH_SHORT).show()
>              } else{
>                   Toast.makeText(this, "이미 가입한 이메일이거나 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
>
>              }
>        }
> ```
> ```auth```의 ```createUserWithEmailAndPassWord(email, password)``` 매소드를 통하여 간단하게 구현할 수 있다.   
> 그리고 여기에 동작 완료시 실행되는 리스너를 붙여, 회원가입 성공 or 실패 시 수행 할 동작을 정의 할 수 있다.
>
> 로그인 과정도 회원가입 과정과 매우 유사하다.
> ```kotlin
>    auth.signInWithEmailAndPassword(email, pwd)
>          .addOnCompleteListener(this) {
>               if(it.isSuccessful){
>                   handleSuccessLogin()
>               } else{
>                   Toast.makeText(this, "로그인에 실패했습니다. 정보를 확인해주세요.", Toast.LENGTH_SHORT).show()
>               }
>           }
> ```
> ```auth```의 ```signInWithEmailAndPassWord(email, password)``` 매소드를 통하여 간단하게 구현할 수 있다.



+ **facebook login**
> 그 다음은, 소셜 로그인인 페이스북 로그인이다. 페이스북 자체 계정 연동을 통해 로그인을 수행하는 것이므로 이메일 로그인과 프로세스가 많이 차이난다.  
> 설정할 것이 많아 공식문서를 두니 이것을 먼저 완료 한 후 코드 작성으로 넘어가야 한다.  
> firebase와 facebook developer에서 기본 설정 후 하단의 링크 따라하기.
> https://developers.facebook.com/docs/facebook-login/android
>> 위 과정 수행 후 faceook developer의 해당 앱에서 앱의 키 해시를 등록해주어야함  
>> 그렇지 않으면 this app has no android key hashes configured. configure your app key hashes at...
>> 같은 키 해시 관련 에러가 발생함  
>> 앱의 해시키를 얻는 방법은 [참고 출처: 나만을 위한 블로그](https://onlyfor-me-blog.tistory.com/406)

```kotlin
private fun initFacebookLoginButton(){
//        binding?.facebookLoginButton?.setPermissions("email", "public_profile")
//        binding?.facebookLoginButton?.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
//            override fun onSuccess(result: LoginResult) {
//                // 성공 시 콜백
//                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
//                auth.signInWithCredential(credential)
//                    .addOnCompleteListener(this@LoginActivity) {
//                        if(it.isSuccessful){
//                            handleSuccessLogin()
//                        } else{
//                            Toast.makeText(this@LoginActivity,"페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//            }
//
//            override fun onCancel() {
//                // 로그인 하다가 취소했을 때 실행되는 콜백
//            }
//
//            override fun onError(error: FacebookException) {
//                //에러시 콜백
//                Toast.makeText(this@LoginActivity,"페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
//            }
//        })

        binding?.facebookLoginButton?.setOnClickListener {
            val loginManager = LoginManager.getInstance()
            loginManager.logInWithReadPermissions(this, listOf("email", "public_profile"))

            loginManager.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult) {
                    // 성공 시 콜백
                    // 페이스북 토큰을 이용하여 authentication을 진해한다.
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) {
                            if(it.isSuccessful){
                                // 이 부분 데이터를 어떻게 넘겨주어야 하는지 방법 찾을 필요가 있음
                                Result.launch(Intent(this@LoginActivity, com.facebook.FacebookActivity::class.java))
                                handleSuccessLogin()
                            } else{
                                Toast.makeText(this@LoginActivity,"페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                override fun onCancel() {
                    // 로그인 하다가 취소했을 때 실행되는 콜백
                }

                override fun onError(error: FacebookException) {
                    //에러시 콜백
                    Toast.makeText(this@LoginActivity,"페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
// onActivityResult의 대응 방안으로 registerForActivityResult를 사용함
private var Result = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
    if(result.resultCode == RESULT_OK){
        val data = result.data
        callbackManager.onActivityResult(result.resultCode, result.resultCode, data)
    }
}

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }
```
> 공식문서에는 ```onActivityResult```를 통한 로그인 데이터 반환을 제안하였지만   
> ```onActivityResult```는 deprecated되어 대응할 필요가 있었다.
> 그래서,```onActivityResult```의 대응 방안인 ```registerForActivityResult```를 통해 콜백 정보를 받아서 데이터를 넘겨주는
> 형식으로 바꿔 보았다. 바꾼 부분이 기능적인 면에서는 동작하지만 facebook 로그인 UI와 상호작용할 떄, 이상한점이 몇 군데 있어서 수정을 해야할 것 같다.


## Firebase Realtime Database 사용하기 [공식문서](https://firebase.google.com/docs/database?hl=ko)
> firebase에서 지원하는 NoSQL 클라우드 데이터베이스이다.  
> 모든 클라이언트에서 실시간으로 데이터가 동기화된다. 앱이 오프라인일 때도 데이터를 사용할 수 있다.  
> 데이터는 JSON tree 형태로 저장된다.
>
> **기본 세팅**  
> 1.firebase 콘솔에서 앱을 만들고 firebase realtime database 항목으로 이동한다.  
> 2.다음과 같은 설정창이 나오는데  우선 테스트 모드로 시작한다. ![img_1.png](img_1.png)  
> 잠금모드는 인증된 사용자만 읽고 쓰기가 가능하기 떄문에 연습단계인 지금은 테스트 모드로 모든사용자가 가능하게 한다.  
> 이러면, 기본 세팅은 끝이나고 코드로 접근하여 데이터를 저장한다.

> 먼저, 데이터를 읽고 쓰려면 ```DataReference``` 객체가 필요하기 때문에 선언해준다
>```kotlin
> private lateinit var userDB: DatabaseReference
>```
> 그 후, 초기화를 해준다
> ```kotlin
> userDB = Firebase.database.reference.child(DBKey.USERS)
> ```
> child() 메소드는 데이터가 있을 위치의 이름을 정해주는 것이라 보면된다.   
> ```child().child()```.. 식으로 체이닝을 하게되면 상위 child가 하위 child의 부모가 되는 트리 형태가 구성된다.  
> child()```에는 ```.addChildEventListener()```가 있는데 child의 경로 상 데이터가 변화 했을 때 작동한다.


> 이 코드는 본 프로젝트에서 쓰인 데이터 쓰기 예제이다.
> ```kotlin
>         userDB.addChildEventListener(object : ChildEventListener {
>           // child() 에 새로운 데이터가 들어왔을떄
>           // 새로운 상대가 생길 시
>            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
>                // 지금아이디가 내가 아니고, 상대방의 like목록에 내가 없고, 상대방의 dislike목록에 내가 없을때인 것만 보여줌
>                // -->한번도 선택 안된 유저
>                if (snapshot.child(DBKey.USER_ID).value != getCurrentUserId()
>                    && snapshot.child(DBKey.LIKED_BY).child(DBKey.LIKE).hasChild(getCurrentUserId())
>                        .not()
>                    && snapshot.child(DBKey.LIKED_BY).child(DBKey.DISLIKE)
>                        .hasChild(getCurrentUserId()).not()
>                ) {
>
>                    val userId = snapshot.child(DBKey.USER_ID).value.toString()
>                    var name = "undecided" // 아이디는 있는데 닉네임 설정을 아직 안했을 때 디폴트값
>                    if (snapshot.child(DBKey.NAME).value != null) {
>                        name = snapshot.child(DBKey.NAME).value.toString() // 닉네임 있으면 재초기화
>                    }
>                    cardItems.add(CardItem(userId, name))
>                    adapter.submitList(cardItems)
>                    adapter.notifyDataSetChanged()
>                }
>            }
>           // child() 에 새로운 데이터가 바뀌었을떄
>            // 상대방의 데이터가 바뀌었을 시
>            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
>                cardItems.find { it.userId == snapshot.key }?.let {
>                    it.name = snapshot.child(DBKey.NAME).value.toString()
>                }
>                adapter.submitList(cardItems)
>                adapter.notifyDataSetChanged()
>            }
>            // child() 에 새로운 데이터가 삭제됐을떄
>            override fun onChildRemoved(snapshot: DataSnapshot) {}
>            // child() 에 새로운 데이터가 이동왔을떄
>            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
>            // 실패 시
>            override fun onCancelled(error: DatabaseError) {}
>
>       })
>```
>  각 메소드 안에서 할 동작을 정의한다. 인자인 ```snapshot```은 child의 데이터를 가리킨다.  
>  snapshot의 child()로 하여 추가 트리 구조를 생성할 수 있다.


> 또다른 동작으로 ```.addListenerForSingleValueEvent()```라고 단일 값에 대한 이벤트만 수행하기위한 메소드가 있다.
> ```kotlin
> currentUserDB.addListenerForSingleValueEvent(object: ValueEventListener{
>      
>       // 값이 바뀌었을 때 실행되는 콜백이다.
>       override fun onDataChange(snapshot: DataSnapshot) {
>       // snapshot - 현재 유저 정보
>       if(snapshot.child(DBKey.NAME).value == null){
>           showNameInputPopUp()
>           return
>       }
>       // 유저 정보를 갱신해라
>       getUnSelectedUsers()
>       }
> 
>       // 에러(취소)시 실행되는 콜백이다.
>       override fun onCancelled(error: DatabaseError) {
>           TODO("Not yet implemented")
>        }
>
> })
> ```
> 또, 다음과 같이 ```setValue()```메소드를 통해서 직접 값을 입력할 수도 있다.
> ```kotlin
>   userDB.child(card.userId)
>         .child(DBKey.LIKED_BY)
>         .child(DBKey.LIKE)
>         .child(getCurrentUserId())
>         .setValue(true)
>```
## yuyakaido / CardStackView 사용하기

![출처-cardstackview 깃헙](https://github.com/yuyakaido/images/raw/master/CardStackView/sample-manual-swipe.gif)
> 위 GIF와 같이 카드를 밀어서 움직이는 것처럼 뷰를 만들 수 있게 해주는 안드로이드 오픈소스 라이브러리이다.

> **사용법**  
> 오픈소스 라이브러리이므로 종속성 추가가 요구된다.
> 앱 수준 gradle의 dependency에 ```implementation "com.yuyakaido.android:card-stack-view:$latest_version"```을 추가해준다.  
> 다음으로, 사용할 뷰(xml)에서 다음과 같이 선언해 준다
> ```xml
> <com.yuyakaido.android.cardstackview.CardStackView
>     android:id="@+id/cardStackView"
>     android:layout_width="match_parent"
>     android:layout_height="match_parent"
>     />
> ```
>
>  기본 셋업
> ```kotlin
> val cardStackView = findViewById<CardStackView>(R.id.card_stack_view)
> cardStackView.layoutManager = CardStackLayoutManager()
> cardStackView.adapter = CardStackAdapter()
> ```
> cardStackView 의 원본을 살펴보면 ```public class CardStackView extends RecyclerView```와  
> 같이 리사이클러 뷰를 상속 받는 것을 확인 할 수 있다. 그렇기 때문에 우리가 cardStackView를 사용할 때도  
> ```LayoutManager```와 ```adapter```를 생성하여 사용하여야한다.
> 리사이클러 뷰와 똑같이 만들어 주기 때문에 링크를 남기고 생략하겠다. [리사이클러뷰](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/BookReview#recyclerview-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

> 주요 동작
> 주 동작은 ```CardStackListener```를 확장하고 내부 메소드를 오버라이드하여 사용한다.
>  ```kotlin
>  override fun onCardDragging(direction: Direction?, ratio: Float) {}
> // 카드가 dragging 될 때 불리는 콜백이다.  
> 
> override fun onCardSwiped(direction: Direction?) {}  
> // 카드가 swipe될 때 불리는 콜백이다.
> 
> override fun onCardRewound() {}
> // 카드가 rewind 될 때 불리는 동작이다.*
>  
> override fun onCardCanceled() {}
> // 카드가 drag 도중 drag를 멈출 때 불리는 콜백이다.
>   
> override fun onCardAppeared(view: View?, position: Int) {}  
> // 카드가 나타날 때 실행되는 콜백이다.
> 
> override fun onCardDisappeared(view: View?, position: Int) {} 
> // 카드가 사라질 때 실행되는 콜백이다.
> 
> > ```
> [*rewind 동작](https://github.com/yuyakaido/CardStackView#rewind)
> [*cancel 동작](https://github.com/yuyakaido/CardStackView#cancel)

> **주의사항**
> implementation 시 안될 때 가 있는데 이때는 setting.gradle의 dependencyResolutionManagement의 repository에 jcenter()를 추가하면
> implement됨
>
> 더 많은 정보는 원본 깃허브에서 찾을 수 있다. [github](https://github.com/yuyakaido/CardStackView)

## 틴더

Firebase Authentication을 통해 이메일 로그인과 페이스북 로그인을 할 수 있음  
Firebase Realtime Database를 통해 기록을 저장하고, 불러올 수 있음 GitHub 에서 OpenSouece Library를 찾아 사용할 수 있음
