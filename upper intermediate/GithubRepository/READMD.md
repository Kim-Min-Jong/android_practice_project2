GitHub Repository app
===

## Coroutine

### process 와 Thread
![img_9](https://user-images.githubusercontent.com/79445881/199433181-44035dbe-a8d9-4797-b60f-3237e4896b2f.png)
> process 는 실행중인 하나의 프로그램(앱)을 뜻한다.    
> process 안에는 한 개의 Heap memory가 할당된다.  
> Heap memory안에는 앱의 실행의 흐름을 위한 여러 개의 Thread가 들어 갈 수 있다.  
> 안드로이드의 기본 Thread는 UI Thread이며, 다른 Thread를 추가할 수 있다.  
> Thread는 하드웨에의 제약을 받는다. CPU에 따라 Thread의 가용 갯수가 달라질 수 있다.  
> Thread에는 stack이 있는데, stack에서 Thread의 메모리를 관리한다.


### Concurrency & Parallelism
![img_10](https://user-images.githubusercontent.com/79445881/199433277-c3705697-f210-472c-af5d-322ff93bd9e4.png)
![img_11](https://user-images.githubusercontent.com/79445881/199433309-43a494fb-1360-4b24-b45d-1c088e97260b.png)
> Concurrency: 작업(Task)이 매우 빠르게 이루어지면서, 작업이 동시에 일어나는 것 처럼 보임
> ThreadPool(병렬처리 최대용량)이 적으면 Concurrency가 좋아야 처리율이 높아진다.

>Parallelism: 작업을 동시에 병렬적으로 시작하여 작업 효율을 높임


### Thread vs Coroutine
> **Thread**
> - Task 단워 : Thread  
    >   -- 각 작업에 Thread를 할당  
    >   -- 각 Thread는 자체 Stack 메모리를 가지며, JVM Stack 영역을 차지함
>
>
> - Context Switching  
    >   -- Blocking: Thread1이 Thread2의 결과가 나올 때 까지 기다려야 한다면    
    >                Thread1 은 Blocking이 되어 사용하지 못한다.


> **Coroutine**
>
> *Task 단위 : Object(Coroutine)*
> - 각 작업에 Object(Coroutine)를 할당
> - Coroutine은 객체를 담는 JVM Heap 영역에 적재됨
>
> *Context Switching -> No Context Switching*
>  - 코드를 통해 Switching 시점을 보장함
>  - Suspend is NonBlcoking: Coroutine1이 Coroutine2의 결과가 나올 때 까지 기다려야한다면
     >    Coroutine1은 Suspend되지만, Coroutine1을 수행하던 Thread는 유효함  
     >     --> Coroutine2도 Coroutine1과 동일한 Thread에서 실행할 수 있음
>
>  비동기 처리 로직을 위해 콜백함수를 연속해서 야기되는 문제인 콜백지옥 해소
>
> *Suspend(일시 중단 함수)*
> - fun 키워드 앞에 ```suspend```키워드를 붙여서 함수를 구성하는 방법
> - 람다를 구성하여 다른 일시 중단 함수를 호출(runBlocking, launch, async...)
>
>
> *Coroutine Dispatcher*
> - 코루틴을 시작하거나 재개할 스레드를 결정하기 위한 도구
> - 모든 Dispatcher는 CoroutineDispatcher 인터페이스를 구현해야함
>
>
> *Coroutine Builder*
>  - async()
     >    - 결과가 예상되는 코루틴 시작에 사용(결과를 반환함)
>    - 전역으로 예외 처리 가능
>    - 결과, 예외 반환 가능한 Defered 타입으로 반환, 추후 await로 처리하여 대기할 수 있는 코드를 구현 할 수 있음
>
> - launch()
    >   - 결과를 반환하지 않는 코루틴 시작에 사용(결과 반환 X)
>   - 자체/자식 코루틴 실행을 취소할 수 있는 Job 객체 반환
>
>  - runBlocking()
     >    - Blocking 코드를 일지 중지(Suspend) 가능한 코드로 연결하기 위한 용도
>    - main함수나 Unit Test 때 많이 사용됨
>    - 코루틴의 실행이 끝날 때 까지 현재 쓰레드를 차단함
>
>

```kotlin
// 인터페이스로 서비스를 구현함 - restful api
interface GithubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Response<GithubRepositoryResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepository(
        @Path("owner") ownerLogin: String,
        @Path("name") repoName: String
    ): Response<GithubRepoEntity>
}

object RetrofitUtil {

    // Retrofit으로 인터페이스로 만든 서비스의 실 구현체를 구현
    val githubApiService: GithubApiService by lazy { getGithubRetrofit().create(GithubApiService::class.java) }
    private fun getGithubRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.GITHUB_API_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(buildOkHttpClient())
            .build()
    }
}
```


```kotlin
// CoroutineContext 생성 (메인 쓰레드)
private val job = Job()
override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

// 메인쓰레드에서 작업시작
 withContext(coroutineContext) {
            var repositoryEntity: GithubRepoEntity? = null
            
            // 네트워크 작업은 메인쓰레드에서 힘드니 IO 쓰레드로 전환 후 작업
            withContext(Dispatchers.IO) {
                val response =
                    RetrofitUtil.githubApiService.getRepository(repositoryOwner, repositoryName)
                if (response.isSuccessful) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        body?.let {
                            repositoryEntity = it
                        }
                    }
                }
            }
            repositoryEntity
        }
```


## 확장함수
> 코틀린에는 기존에 정의된 클래스에 custom함수를 추가할 수 있는 기능이있다.
> 확장함수는 ```fun 클래스이름.함수이름(인자타입): 리턴타입 { 구현부 }```로 정의 할 수 있다.
>
```kotlin
//ex


// 안드로이드에서 dp 값을 px값으로 변환하는 예제이다.
fun Float.fromDpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

// Toast 메세지를 많이 사용한다면 다음과 같이 확장함수로 만들어서 코드양을 줄여볼 수 있다.
private fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
```

> 이와 같이 기존 클래스에 본인이 원하는 재사용 가능한 함수를 만들어 두면   
> 코드 양을 줄일 수 있고, 편리성을 증대 시킬 수 있다.
>


## Room
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/basic/basic/Calculator#room-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

```kotlin
// DAO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fc.githubrepository.data.entity.GithubRepoEntity

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insert(repo: GithubRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList: List<GithubRepoEntity>)

    @Query("SELECT * FROM githubrepository")
    suspend fun getHistory(): List<GithubRepoEntity>

    @Query("SELECT * FROM githubrepository WHERE fullName = :fullName")
    suspend fun getRepository(fullName: String): GithubRepoEntity?

    @Query("DELETE FROM githubrepository WHERE fullName = :fullName")
    suspend fun remove(fullName: String)

    @Query("DELETE FROM githubrepository")
    suspend fun clearAll()

}
```

```kotlin
// Room DB 추상 객체 (DAO 설정, 테이블 클래스 설정)
@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {
    abstract fun repositoryDao(): SearchHistoryDao
}
```

```kotlin
// 실제 DB 객체  (Room Builder형태로 DB 생성)
object DatabaseProvider {
    private const val DB_NAME = "github_repository_app.db"
    fun provideDB(applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        SimpleGithubDatabase::class.java, DB_NAME
    ).build()
}
```

## RecyclerView
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/BookReview#recyclerview-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)



### 순서
1. 깃허브 연결 및 Auth토큰 받아오기
2. 검색 기능추가하기
3. 즐겨찾기 기능 추가
4. 코드 다듬기  