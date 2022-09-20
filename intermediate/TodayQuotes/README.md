Today's Quotes
===
## View Pager2
> **ViewPager**는 좌우 스크롤을 통해 화면을 넘길 수 있는 기능을 제공한다.  
> 화면을 전환하여 보여주기 떄문에 *fragment* 사용하여 구현하고, 여러 fragment중 하나를  
> 선택하는 형태의 view이기 때문에 recyclerview와 같이 어댑터를 사용하여 데이터를 분배해주어야한다.

> **ViewPager2**는 *ViewPager*의 개선된 버전으로 향상된 기능을 제공하고, *ViewPager* 사용 시  
> 발생하는 문제들을 해결한다. 그래서 요즘에는 **ViewPager2**를 주로 사용한다.
>
>
>
> ![출처-강남언니 기술블로그](https://static.blog.gangnamunni.com/files/24aeb763-3d0b-4d03-acbf-19cc5a9fd50d)
>
> **ViewPager2**는 initialize시 *RecyclerView*를 사용하므로 recyclerview 어댑터 사용방식대로 사용가능하다.  
> RTL(right-to-left) 스크롤을 지원한다.  
> vertical swipe를 지원한다. (기존에는 horizontal 하게만 지원됨)

>**PageTransformer**  
> viewPager에서 view swipe시 효과를 주는 기능이다.  
> ```setPageTranformer{ page, position -> }``` 메소드를 통하여 지정할 수 있다.  
> position의 상대 위치를 통하여 효과를 준다.  
> position은 화면 중앙을 기준으로 특정 페이치의 위치를 나타낸다.
> ![](C:\Users\김민종\Desktop\pager.JPG)
>
> 위 사진과 같이 페이지가 화면을 채우면 position 은 ```0```이 된다. 이때, 페이지가 오른쪽으로  
> 벗어나면 위치값은 ```1```이 된다. 사용자가 페이지 ```1```과 ```2```의 증간으로 스크롤을 하면  
> 페이지 ```1```의position의 위치값은 -**0.5**가 되고 페이지 ```2```의 위치값은  **0.5**가 된다.  
> 이와 같이, 페이지 기준으로 상대적인 위치에 따라, ```setAlpha()```, ```setTranslationX()```,   
> ```setScaleY()```와 같은 메소드로 페이지 속성을 설정하여 슬라이드 애니메이션을 만들 수 있다.

## Firebase
### remote config
>  **remmote config**는 앱 업데이트를 게시하지 않아도 하루 활성 사용자 수 제한 없이 무료로 앱의 동작과 모양을 변경할 수 있게 해주는 기능이다.

#### 주요기능
|    앱 사용자층에 변경사항을 빠르게 적용    |      서버 측 매개변수 값을 변경하여 앱의 기본 동작과 모양에 변화를 줄 수 있다. 예를 들어 앱 업데이트를 게시하지 않고도 앱의 레이아웃 또는 색상 테마를 변경하여 계절별 프로모션을 지원할 수 있다.       |
|:--------------------------:|:------------------------------------------------------------------------------------------------------------------------:|
| **사용자층의 특정 세그먼트에 앱 맞춤설정**  |       **remote config을 사용하면 앱 버전, 언어, Google 애널리틱스 잠재고객, 가져온 세그먼트를 기준으로 분류된 사용자층 세그먼트에 앱의 다양한 사용자 환경을 제공할 수 있다.**        |
|   **A/B 테스트를 실행하여 앱 개선**   | **원격 구성의 임의 백분위수 타겟팅 기능과 Google 애널리틱스를 함께 사용한 A/B 테스트를 통해 앱을 개선할 수 있다. 사용자층을 나눈 세그먼트별로 개선사항을 검증한 후 전체 사용자층에 적용할 수 있다.**  |


#### 기본 동작 원리
> remote config 는 파라미터 값 가져오기 및 캐싱 등의 중요한 작업은 클라이언트 측에서 처리하며,
> 새 값이 활성화되어 사용자 경험에 영향을 주는 작업등은 개발자가 직접 제어한다.
> 이것을 통해 변경 시점을 제어하여 원활한 앱 사용 경험을 보장할 수 있다.

#### 사용시 주의 사항
> + 사용자가 승인을 해야하는 앱 업데이트엔 사용하면 안된다. 무단 업데이트는 앱의 신뢰성을 해칠 수 있다.
> + 키 또는 매개변수 값에 중요 데이터를 저장하면 안된다. remote config 설정에 저장된 값은 3자가 해독할 수 있다.
> + remote config를 통해 앱의 타겟 플랫몸에서 요구하는 조건을 우회하면 안된다.
>


```kotlin
// firebase remoteConfig 객체를 가져온다.
val remoteConfig = Firebase.remoteConfig

/***
remote config의 설정을 한다.
minimunFetchIntervalInSeconds 속성은 데이터를 Fetch할 인터벌을 지정하는 부분이다.
remoteConfig을 통해서 한번 가저온 데이터는 인터벌로 정한 시간 동안 다시 가저 오지않는다.
 
파이어베이스는 앱에서 단기간에 가져오기를 많이 수행하면 가져오기 호출이 제한된다.
그래서 보통 이 현상을 방지하기 위해 인터벌 시간을 60분이상 주는 경우가 많다.
본 프로젝트에서는 가벼운 데이터이기떄문에 0으로 했지만, 실제 서비스를 수행할 때는 60분이상 즉 3600 이상 부여하는것을 추천하는 바이다.
기본값은 12시간이다.
 ***/
remoteConfig.setConfigSettingsAsync(
        remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
)

/***
 비동기 방식으로 데이터를 가져온다. 여기서의 데이터는 json이기 때문에
 view에 바인딩 되어야 할 데이터를 json에서 파싱해서 변수에 담아두었다.
 ***/
remoteConfig.fetchAndActivate().addOnCompleteListener {
    progressBar.visibility = View.GONE
    if (it.isSuccessful) {
        val quote = parseQuotesJson(remoteConfig.getString("quotes"))
        val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")

        displayQuotesPager(quote, isNameRevealed)

    }
}
```

```kotlin
// 파싱 과정
private fun parseQuotesJson(json: String): List<Quote> {
    val jsonArray = JSONArray(json)
    var jsonList = emptyList<JSONObject>()
    for (i in 0 until jsonArray.length()) {
        val jsonObj = jsonArray.getJSONObject(i)
        jsonObj?.let {
            jsonList = jsonList + it
        }
    }

    return jsonList.map {
        Quote(it.getString("quote"), it.getString("name"))
    }
}
```