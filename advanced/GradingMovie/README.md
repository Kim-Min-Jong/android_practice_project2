Movie Grade
===

## MVP
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/TrackingDelivery#mvp)

## Jetpack Navigation
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/TrackingDelivery#jetpack-navigation-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C)

## FireStore [공식문서](https://firebase.google.com/docs/firestore)

Cloud Firestore는 Firebase 및 Google Cloud의 모바일, 웹, 서버 개발을 위한 유연하고 확장 가능한 데이터베이스이다.  
Firebase 실시간 데이터베이스와 마찬가지로 실시간 리스너를 통해 클라이언트 앱 간에 데이터 동기화를 유지하고 모바일 및 웹에 대한 오프라인 지원을 제공하므로 네트워크 지연 또는 인터넷 연결에 관계없이 작동하는 반응형 앱을 구축할 수 있다.  
Cloud Firestore는 Cloud Functions를 비롯한 다른 Firebase 및 Google Cloud 제품과도 원활하게 통합된다.

* [주요 기능] (https://firebase.google.com/docs/firestore#key_capabilities)
    * 유연성
    * 표현 쿼리
    * 실시간 업데이트
    * 오프라인 지원
    * 확장 가능


* 기본
>  **데이터 추가**  
> Cloud Firestore는 컬렉션에 저장되는 문서에 데이터를 저장한다. Cloud Firestore는 문서에 데이터를 처음 추가할 때 암묵적으로 컬렉션과 문서를 생성한다. 컬렉션이나 문서를 명시적으로 만들 필요가 없다.  
> collection -> (document -> (collection) ->document)..  컬렉션 안에 문서.. 식으로 중첩되며 추가 가능하다.

// 트랜잭션을 통해 데이터를 추가한다. (데이터 정합성 때문에)
```kotlin
// 리뷰 추가 메소드
override suspend fun addReview(review: Review): Review {
        // 먼저 firestore collectio에서 정보 문서를 가져옴
        val newReviewReference = fireStore.collection("reviews").document()
        val movieReference = fireStore.collection("movies").document(review.movieId!!)

        /*
          firstore transaction (DB transaction 특성을 갖고 있음)
          트랜잭션 특성에 따라 (실패면 전과정 롤백, 성공이면 모두 성공) 필요 시 사용한다.
         */
        fireStore.runTransaction { transaction ->
            // movies collection에서 객체를 가져온다.
            // get() transaction에 앞에 있지않으면 에러 발생
            val movie = transaction.get(movieReference).toObject<Movie>()!!

            // 저장된 점수
            val oldAverageScore = movie.averageScore ?: 0f
            val oldNumberOfScore = movie.numberOfScore ?: 0
            val oldTotalScore = oldAverageScore * oldNumberOfScore

            // 새 점수 (이전 총점에서 리뷰 쓴 평점을 더해 평균)
            val newNumberOfScore = oldNumberOfScore + 1
            val newAverageScore = (oldTotalScore + (review.score ?: 0f)) / newNumberOfScore
            /*
                firestore 트랜잭션에서는 데이터 경합(동시 접근)시 실패나 오류를 반환해서
                데이터무결성을 보장한다.
             */
            // set을 통해 업데이트 한다.
            transaction.set(
                movieReference,
                movie.copy(
                    numberOfScore = newNumberOfScore,
                    averageScore = newAverageScore
                )
            )

            // set을 통해 업데이트 한다.
            transaction.set(
                newReviewReference,
                review,
                // 바뀐 부분을 merge 한다. (default는 덮어쓰기)
                SetOptions.merge()
            )
        }.await()

        return newReviewReference.get().await().toObject<Review>()!!
    }

```


> **데이터 가져오기**   
> (읽는 것은 동시 읽기해도 상관없으므로 트랜잭션 x)
```kotlin
    // 모든 영화 정보 가져오기
    override suspend fun getAllMovies(): List<Movie> =
        // firestore의 movies에서 다 가져오고
        fireStore.collection("movies")
            .get()
            // suspend 하기 떄문에 await 처리
            .await()
            // firestore에서 가져온 것을 Movie 형태로 변환
            .map { it.toObject<Movie>() }

    // 특정 영화 정보 가져오기
    override suspend fun getMovies(movieIds: List<String>): List<Movie> =
        fireStore.collection("movies")
             // 필드 값(@DocumentId)이 movieIds에 속하는지 판단해서 (단일 인덱싱)
            .whereIn(FieldPath.documentId(), movieIds)
            // 가져옴옴
           .get()
            .await()
            .map { it.toObject<Movie>() }
```

* [**인덱싱**](https://firebase.google.com/docs/firestore/query-data/index-overview)  
  데이터베이스 성능의 중요한 요소 중 하나
  데이터베이스에 쿼리를 보내면 인덱스를 사용하여 요청한 위치를 **빠르게** 찾을 수 있다.
    * 종류
        * [단일 필드 색인](https://firebase.google.com/docs/firestore/query-data/index-overview#single-field_indexes) (한 가지 속성에 대해서만 인덱싱)
        * [복합 필드 색인](https://firebase.google.com/docs/firestore/query-data/index-overview#composite_indexes) (여러가지 속성을 동시 인덱싱)

추가적인 내용은 공식문서를 통해 공부..(내용이 많다..)


## Koin
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/ToDoList#di-vs-service-locator)

```kotlin
// 아키텍쳐 컴포넌트 별로 의존성 주입 분리

// IO작업을 실행하는데 최적화 되있는 디스패처 주입
// Dispatcher - 자신이 관리하는 쓰레드 풀 내의 쓰레드의 부하 상황에 맞춰 코루틴 배분하는 역할
val appModule = module {
    single { Dispatchers.IO }
}

// data layer 주입
val dataModule = module {
    single { Firebase.firestore }

    // api
    single<MovieApi> { MovieFireStoreApi(get()) }
    single<ReviewApi> { ReviewFireStoreApi(get()) }
    single<UserApi> { UserFirestoreApi(get()) }

    // repository
    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }

    // shared preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }
}

// domain layer
val domainModule = module {
    // usecase 주입
    factory { GetRandomFeaturedMovieUseCase(get(), get()) }
    factory { GetAllMoviesUseCase(get()) }
    factory { GetAllMovieReviewsUseCase(get(),get()) }
    factory { GetMyReviewedMoviesUseCase(get(), get(), get()) }
    factory { SubmitReviewUseCase(get(), get()) }
    factory { DeleteReviewUseCase(get()) }
}

// presenter
val presenterModule = module {
    // scope - 명시된 Scope 생명주기에 영속적인 객체를 생성해서 제공    
    // fragment - presenter 
    scope<HomeFragment> {
        scoped<HomeContract.Presenter> { HomePresenter(getSource(), get(), get()) }
    }
    scope<MovieReviewsFragment> {
        scoped<MovieReviewsContract.Presenter> { (movie: Movie) ->
            MovieReviewsPresenter(movie, getSource(), get(), get(), get())
        }
    }
    scope<MyPageFragment> {
        scoped<MyPageContract.Presenter> { MyPagePresenter(getSource(), get()) }
    }
}
```