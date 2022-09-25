Book Review
===

## viewBinding 사용하기
> 이전의 activity나 fragment상의 코드에서 뷰에 접근하기 위해 ```findViewById()```메소드를 사용하였다.
> ```findViewById()```는 파라미터에 view의 xml의 id에 접근하여서, 그 뷰를 코드상에서 제어할 수 있도록 하였다.
>
> 하지만, ```findViewById()```에는 여러가지 문제가 있었다.
> 뷰나 컴포넌트들에 접근을 하려면, 모든 뷰,컴포넌트에 ```findViewById()```를 선언하여 사용해야되어서
> 비슷한 코드가 계속 반복되어 코드가 길어지고 반복되었다.
> 또, ```findViewById()```는 직접 뷰타입을 작성해야 하므로 *type cast error*를 유발할 수 있었으며,  
> 파라미터에 잘못된 id 값을 작성하여 *NullPointerException*의 위험성도 있다.
>
> 또, ```findViewById()```의 동작원리에서도 문제점이 있었다. 이것은 xml의 구조를 생각해보면 쉽게 알 수 있는데,
> xml은 태그 구조로 이루어져 있어 태그안의 태그안의 태그... 등 다층 구조의 태그로 이루어져 있다.
> 그래서 태그 구조는 트리형태로 볼 수 있는데, ```findViewById()```는 이 xml트리를 순회하면서 파라미터로 받은 id값을 찾는다.  
> 트리구조가 단순할 때는 큰 영향이 없지만, 시간이 갈 수록 앱은 복잡해지고 화면구조도 복잡해졌다. 그러면서 당연히 xml트리도 깊고
> 복잡해지는데, 이걸 순회하려면 속도가 오래걸려 문제점이 발생하였다.
>
> 이러한 문제점들을 해결하기 위해, **viewBinding**이라는 개념이 나왔다.
> **viewBinding**은 gradle 파일에서 설정해주는 것만으로도, 작성된 레이아웃 파일(xml)들을
> **바인딩 클래스**로 모두 자동변환 해준다.  ```ex)activity_main.xml = ActivityMainBinding```
>
> 이 바인딩 클래스 객체를 만들어 뷰의 컴포넌트들에 직접적으로 접근하여 사용할 수 있다.
```kotlin
// ex) MainActivity

class MainActivity : AppCompatActivity() {
    // binding객체를 lateinit하게
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        // binding.root = R.layout.activity_main 
        setContentView(binding.root)

        // 다음과 같이 컴포넌트에 바로 접근가능    
        binding.component.XXX...
    }
}
```

## Retrofit2 사용하기 (API 호출)
> 오픈소스 라이브러리로, REST API 통신을 위한 기능들을 담은 통신 라이브러리이다.
> ```okHttp```라이브러리의 상위 구현체로, Retrofit은 OkHttp를 네트워크 계층으로 활용하고 그 위에 구축되어있다.  
> 다음과 같은 장점이 있다.
> > 빠르다 - AsyncTask보다 3~10배의 성능을 보여준다.  
> > 가독성 - Annotation(애노테이션) 사용으로 코드의 가독성이 뛰어나다. 그래서 직관적인 코딩이 가능하다.  
> > 간단한 유지 보수 - Retrofit은 서버 연동 시 주로 주고받는 데이터인 JSON, XML을 자동을 파싱해주는 Converter 연동을 지원해준다.

> **기본 사용법**  
> 오픈소스 라이브러리이므로 의존성 추가가 필요하다.  
> ```implementation 'com.squareup.retrofit2:retrofit:$retrofit_version```  
> ```implementation 'com.squareup.retrofit2:converter-gson:$retrofit_version```  
> (converter는  JSON 타입의 결과를 객체로 자동 파싱해준다.)
>
>
> *모델 생성*
> ```
> data class Book(
>    @SerializedName("isbn") val id: String,
>    @SerializedName("title") val title: String,
>    @SerializedName("description") val description: String,
>    @SerializedName("price") val priceSales: String?,
>    @SerializedName("image") val coverSmallUrl: String,
>    @SerializedName("link") val mobileLink: String
>```
> Model 은 서버 연동을 위해 사용하는 데이터 추상화 클래스이다. 위에서 이야기했던 convertor가 JSON 데이터를 자동으로 파싱하고, 객체를 생성한 후 모델에서 정의한 변수에 데이터를 담아준다. 보통 DTO 클래스라고 부른다.
>
>
>
> *서비스 생성*
> ```kotlin
> interface BookService {
>   //annotation으로 http method를 정하고 path를 입력
>   @GET("/v1/search/book.json")
>   fun getBooksByName(
>       // 전송시 필요한 헤더를 붙임
>       @Header("X-Naver-Client-Id") id: String,
>       @Header("X-Naver-Client-Secret") secretKey: String,
>       // path의 query부분을 붙임    /path?query=...
>       @Query("query") keyword: String
>   ): Call<SearchBookDto>
> }
>```
> Retrofit의 핵심이라고 할 수 있는 부분이다.  
> 해당 함수에서 annotation 으로 HTTP Method 를 지정하고, 서버에 전송할 데이터를 추가하면 그 정보에 맞게 서버를 연동할 수 있는 Call 객체를 자동으로 생성하는 구조이다.  
> 즉, 어떤 형태로 어떻게 통신을 할건지 개발자가 정해주면 Retrofit 이 알아서 구현해준다는 의미이다.
>
>
>
>
> *retrofit 실행*
> ```kotlin
> val retrofit = Retrofit.Builder()
>        .baseUrl("https://openapi.naver.com/ ")
>        .addConverterFactory(GsonConverterFactory.create())
>        .build()
>
> bookService = retrofit.create(BookService::class.java)
>```
> 먼저, retrofit의 builder를 만든다. 통신을 할 url을 ```baseUrl()```메소드를 통해 지정해준다.  
> 그리고, 자동으로 gson으로 convert해 줄 설정을 한다.
> 그리고, 위에서 만든 service를 ```retrofit.create()```해서 통신할 실 객체를 만들어 준다.
>
>
```kotlin
        bookService.getBooksByName(keys.getString(CLIENT_ID), keys.getString(CLIENT_SECRET), "책")
            .enqueue(object: Callback<SearchBookDto>{
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    if(response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS")
                        return
                    }
                    //.. 동작 정의
                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    Log.e(TAG, t.toString())
                    //.. 동작 정의
                }

            })
```
> 마지막으로, 서비스를 실행한다. ```enqueqe()```메소드를 통해 비동기 실행을 하고 통신이 완료 되었을 시,  
> 이벤트 처리를 하기위해 Callback리스너를 등록한다.  
> Callback리스너 ```onResponse()```는 통신 성공시 실행되는 함수이다.  
> ```onFailure()```는 통신 실패시 실행되는 함수이다.  
> 각각의 오버라이드 메소드에서 통신 후 수행할 동작을 정의해 주면된다.



## Glide 사용하기(이미지 로딩 라이브러리)
> 안드로이드 오픈소스 라이브러리로, 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리이다.  
> 사용 방법도 간단하고 확장성도 넓어서 많이 사용되고 있는 라이브러리이다.  
> 이미지, Gif등의 다양한 API를 사용할 수 있다.  
> 기본적으로는 커스텀하게 만들어진 HttpUrlConnection 기반이지만, Volley나 OkHttp 라이브러리를 사용할 수 있는 플러그인도 지원한다.

> **기본 사용법**
>
> 오픈소스 라이브러리이기 때문에 의존성 추가가 필수이다.  
> ```     implementation 'com.github.bumptech.glide:glide:$glide_latest_version'```
> ``` annotationProcessor 'com.github.bumptech.glide:compiler:glide_latest_version'```을  
> 앱 수준 *dependencies*에 추가해준다.  
> 추가 설정으로, 인터넷에서 직접 가져온다면 ```androidmanifest.xml```에   
>```<uses-permission android:name="android.permission.INTERNET" />``` 권한을 추가해야한다.
>
>
>
>```kotlin
>Glide.with(binding.coverIv.context)
>     .load(model?.coverSmallUrl.orEmpty())
>     .into(binding.coverIv)
>```
> 주로, Glide객체 선언후 메소드 체이닝을 통해 사용한다.   
> ```with(context)```: view, activity 등에서 context를 가져온다.  
> ```load(Bitmap, Drawable, String, Uri, File, ResourId(Int), ByteArray) ```:  
> 다양한 파라미터 타입을 받고, 해당 파라미터의 이미지를 불러온다.  
> ```into(view)```: 이미지를 보여줄 view를 지정한다.
>
> 추가적인 메소드
> > 이미지 전/후 처리  
> > ```placeholder(Uri..etc).``` : Glide 로 이미지 로딩을 시작하기 전에 보여줄 이미지를 설정한다.  
> > ```error(Uri..etc)``` : 리소스를 load시 에러가 발생했을 때 보여줄 이미지를 설정한다.  
> > ```fallback(Uri..etc)``` : load할 url이 null인 경우 등 비어있을 때 보여줄 이미지를 설정한다.
>
> 이외의 이미지 캐싱, GIF다루기 등 다양한 메소드가 있다.
> 공식문서를 참조하면 좋을 것 같다. [공식문서](https://bumptech.github.io/glide/)


## Room 사용하기
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/basic/basic/Calculator#room-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

+ migration 대응
> 프로그램을 짜다보면 요구사항의 변경으로 인해 DB Table의 구조가 바뀌는 경우가 많다.
```kotlin
@Database(entities=[History::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

// --->

@Database(entities=[History::class, Review::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}
```
> 위 코드처럼 데이터베이스의 테이블이 추가되고 그대로 실행하게 되면, 이전에 사용되던 데이터베이스와 지금 사용하려는 데이터베이스 내부가 변경되어
> migration error가 발생하게 된다.  
> 이때, migration을 통해 데이터베이스 버전을 올려주어 에러를 해결해 줄 수 있다.


```kotlin
    val migration_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
        }

    }
    return Room.databaseBuilder(context, AppDatabase::class.java, "BookSearchDB")
//        .fallbackToDestructiveMigration().build()
        .addMigrations(migration_1_2).build()
```
> 먼저, *Migration*객체를 선언한다. *Migration*객체의 파라미터로 이전버전, 다음버전을 넣는다.  
> 그리고 *Migration*객체의 *migrate*메소드를 오버라이드하여 데이터베이스 테이블을 재정의한다  
> 마지막으로 *Room.databaseBuilder*를 build하는데 ```addMigrations()```메소드를 이용하여  
> 방금 생성한 *migration*을 파라미터로 전달하여 *migrate*해준다.
>
> 주석처리한 ```fallbackToDestructiveMigration()``` 메소드는 *migrate*객체를 만들지 않고 바로  
> 다음버전으로 migrate해주는 메소드이다.  
> 코드 작성상, 편리함은 있지만, 이 메소드는 이전 데이터베이스를 아예 삭제 후 재생성하기 때문에
> 데이터 손실이 있다. 데이터 손실을 방지하고자 하면 해당 메소드는 사용을 **절대**하면 안된다.
> 데이터 손실이 있어도 상관없을 경우, 편하게 사용하여도 된다.



## Naver Open API 사용하기
> 네이버 책 검색 api를 이용하였다  
> [공식문서](https://developers.naver.com/docs/serviceapi/search/book/book.md#%EC%B1%85)


## RecyclerView 사용하기
> android jetpack 구성요소 중 하나이다.  
> 대량의 데이터를 동적, 효율적으로 보여주기위한 뷰 레이아웃이다.  
> View의 item들을 adapter를 통해 binding해준다.
> listView는 item을 만들때 마다 새로 만들지만, recyclerView는 이전 item이 사용하던 공간을 재활용(그 자리에 binding)하여 사용한다.  
> [기본사용법-출처:개발자를위한레시피](https://recipes4dev.tistory.com/154)


## 책 리뷰앱
+ Naver Open API를 통해 베스트셀러 정보를 가져와서 화면에 그릴 수 있음
+ Naver Open API를 통해 검색어에 해당하는 책 목록을 가져와서 화면에 그릴 수 있음
+ Local DB를 이용하여 검색기록을 저장하고 삭제할 수 있음
+ Local DB를 이용하여 개인 리뷰를 저장할 수 있음