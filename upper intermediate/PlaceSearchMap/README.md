Place Search
===

## naver Map api 사용하기
[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/Airbnb#naver-map-api-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C)  
주의 사항 잘 살펴보기


## T Map api 사용하기 (POI 데이터) [공식문서](https://tmapapi.sktelecom.com/main.html#webservice/docs/tmapPoiSearch)
> T Map에서 제공하는 POI 데이터를 사용하여 위치 검색을 하고, 그 주변의 시설정보를 가져와서
> 사용하였다.  
>  POI api는 sk developer console에서 앱 등록 후 url 접근을 통해 바로 사용할 수 있다.
> 공식문서에 기본정보 및 반환형식등이 명시되어있다.


## Coroutine 기초 및 Retrofit과 사용하기
```kotlin
// 메인컨텍스트에서 시작
launch(coroutineContext) {
    try {
        // IO 컨택스트 전환
        withContext(Dispatchers.IO) {
            val response = RetrofitUtil.apiService.getSearchLocation(keyword = keyWord)
            if (response.isSuccessful) {
                val body = response.body()
                // 데이터 받기 성공시 다시 메인 컨텍스트 전환후 동작
                withContext(Dispatchers.Main) {
                    Log.e("Response", body.toString())
                    body?.let { searchResponse ->
                        setData(searchResponse.searchPoiInfo.pois)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(this@MainActivity, "검색하는 과정에서 에러가 발생했습니다. : ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
```
> 코루틴은 사용하기는 편하지만, 동시성 제어를 위한 도구인 만큼 제대로 다루지 못하면 데이터의 손실 및 앱 강제 종료등 다양한 문제들을 불러올 수 있다.  
>  아직 이해를 못한 부분도 많아 추가 적인 공부 후에 차근차근 작성을 해야 할 것 같다고 생각한다.
> 코루틴에 관한 글로 해당 [블로그](https://kotlinworld.tistory.com/140) 에서 여러가지 정보를 얻고 있다.


## Parcelable Intent 사용하기
> 안드로이드에서 Intent를 통해서 activity로 데이터를 전달할 때, 기본 타입이 아닌 객체 자체를 전달해야할 때가 있다.
> 이떄, 사용 할 수 있는 것이 Parcelable 이라는 인터페이스 이다. Serializable보다는 복잡하지만, 더 빠른 속도를 보여준다.
>
> 코틀린에서는 parcelable 인터페이스를 어노테이션으로 만들어 바로 지정할 수 있게 해준다.
> 먼저, 앱 수준 gradle의 plugin에 다음을 추가해준다.
> ```groovy
> plugins {
>   id 'kotlin-parcelize'
> }
> ```
>  이 플러그인만 추가 하면, 보낼 데이터 객체 모델에 어노테이션을 적용하여 parcelable하게 만들 수 있다.
>
> ```kotlin
> import android.os.Parcelable
> import kotlinx.parcelize.Parcelize
>
> @Parcelize
> data class LocationLatLngEntity (
>   val latitude: Float,
>   val longitude: Float
> ): Parcelable
> ```

### 안드로이드 스튜디오에서 커밋시, api 서비스에 필요한 키 값 숨기기
> 외부 api를 사용하려고 보면, 특정 키 값이 필요한 경우가 있다.
> 키 값은 고유 값으로, 외부 노출시 보안 문제의 위험성이 있다. 그래서 깃을 사용 할 시에는
> 키 값을 가려줄 필요가 있다.
>
> 키 값을 문자열 그대로 사용하지 않고, json파일이나 xml파일로 따로 리소스를 만들어서
> 코드를 통해 json, xml에 접근하여 키 값을 가져와서 사용한다.  
> 그리고, json, xml 파일등은 고유의 파일명을 지정하여 gitignore 파일에 명시하여 키 값의 외부 노출을 막는다.