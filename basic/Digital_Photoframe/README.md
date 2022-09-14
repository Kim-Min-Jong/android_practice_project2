전자액자
====
## Layout을 그리는 법
+ 뷰가 겹칠 때
    - 먼저 선언한 뷰가 뒤로 가고 후에 선언한 뷰가 앞으로 온다.


+ 가로화면으로 그리기
``` kotlin
AndroidManifest.xml에서 activity의 screenOrientation="landscape"로 변경해주면
해당 액티비티는 가로화면적용

<activity
      android:name=".XXXActivity"
      android:screenOrientation="landscape"
       android:exported="false" />
```

## Android Permission 사용하기
```
안드로이드는 데이터 보안을 위해 앱이 외부의 리소스나 정보가 필요할 때는 권한을 받아야만한다.
그래서 사진첩등 외부 리소스 접근을 위해 권한을 요청하여야한다.
```
###기본원칙
런타임 권한을 요청하기 위한 기본 원칙은 다음과 같다.

>사용자가 권한이 필요한 기능과 상호작용하기 시작할 때 컨텍스트에 따라 권한을 요청한다.  
>사용자를 차단하지 않는다.  
>항상 권한과 관련된 교육용 UI 흐름을 취소하는 옵션을 제공한다.  
>사용자가 기능에 필요한 권한을 거부하거나 취소하면 권한이 필요한 기능을 사용 중지하는 등의 방법으로 앱의 성능을 단계적으로 저하시켜 사용자가 앱을 계속 사용할 수 있도록 한다.  
>시스템 동작을 가정하지 않는다. 예를 들어 동일한 권한 그룹에 권한이 표시된다고 가정하지 않는다.  
>권한 그룹은 앱이 밀접하게 관련된 권한을 요청할 때 시스템에서 사용자에게 표시하는 시스템 대화상자의 수를 최소화하는 데만 도움이 된다.

### 권한 워크플로우
![](https://developer.android.com/static/images/training/permissions/workflow-runtime.svg?hl=ko)
``` 
정리
1. manifest에 권한을 선언
2. UI/UX 설계
3. 사용자가 권한을 요청할 때까지 대기
4. 먼저 권한이 부여가 되어있는지 확인
5-a. 권한이 이미 부여되어 있다면, 리소스 접근
5-b. 권한이 부여되어 있지 않다면, 권한이 왜 부여되어야 되는지에 대한 설명창 제공
6. 사용자가 설명창을 본 후, 권한 동의 --> 리소스 접근
```
```kotlin
권한 요청 예제

when{
     // 권한 확인
     ContextCompat.checkSelfPermission(
          this, android.Manifest.permission.READ_EXTERNAL_STORAGE
     ) == PackageManager.PERMISSION_GRANTED -> {
            // 권한이 부여되었을때 실행할 행동
            ...
     }
     ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
           // 교육용 팝업 확인후 권한 팝업 띄우는 기능
           showPermissionContextPopUp()
     }
     else -> {
          // 위의 두 경우가 모두 아닐 때, 권한을 재요청
          ActivityCompat.requestPermissions(
              this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000
          )
     }
}
```
```kotlin
권한 응답 처리 예제

val requestPermissionLauncher =
    registerForActivityResult(RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 있으니 할 행동 정의
            ...
        } else {
            // 권한이 없으니 추후 할 행동 정의(ex) 권한 재요청)
            ...
        }
    }

```
## View Animation 사용하기
> 특정 view에 대해서만 애니메이션을 준다. 연관 파일은 res/anim에 저장한다.  
> 주된 속성은 다음과 같다.  
> ```Alpha: 투명도, 주로 fade-in, out효과를 줄 때 사용한다. ```  
> ```Rotate: 회전을 주고 싶을 때 사용 ```  
> ```Scale: 크기 변환 속성, X, Y축 따로 설정 가능 ```  
> ```Translate: 위치 변경할 때 사용 ```

```kotlin
ex)
View.animate()
     .alpha(1.0f)
     .setDuration(1000)
     .start()
```

## Android LifeCycle 알아보기
+ [https://developer.android.com/guide/components/activities/activity-lifecycle?hl=ko](https://developer.android.com/guide/components/activities/activity-lifecycle?hl=ko)
  ![](https://developer.android.com/guide/components/images/activity_lifecycle.png?hl=ko)
> 간단하게 잘 설명된 블로그 글이 있어서 링크를 남긴다.
> + [android-lifecycle 정리 글](https://kotlinworld.com/46?category=918951)

## Content Provider
+ SAF(Storage Access Framework)
> 다음과 같은 이유로 starActivityForResult, onActivityResult가 deprecated되었음
```
AndroidX Activity와 Fragment에 도입된 Activity Result API 사용을 적극 권장.
결과를 얻는 Activity를 실행하는 로직을 사용할 때, 메모리 부족으로 인해 프로세스와 Activity가 사라질 수 있다. (특히 카메라 같은 메모리를 많이 사용하는 작업은 소멸 확률이 굉장히 높다.)
```
> 대체 수단필요
```kotlin
{
    val getGalleryImageLauncher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data // 선택한 이미지의 주소(상대경로)
                
                // 이미지를 가져와서 할 행동 정의
                ...
            }
        }
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    getGalleryImageLauncher.launch(intent)
}
```

> 위와 같이 registerForActivityResult로 대체할 수 있음

### trouble shooting (20220914 22:50)
> 위와 같이 한 메소드로 구성후 초기화 했더니  
> ```LifecycleOwner com.fc.digital_photoframe.MainActivity@ef76d2 is attempting to register while current state is RESUMED. LifecycleOwners must call register before they are STARTED.```  
> 오류발생  
> 확인 결과 registerForActivityResult는 액티비티 상태가 STARTED 이전에 초기화가 되어야하는데 RESUME상태에서 초기화가 되어 에러 발생
>
> 해결 > ```getGallaryImageLauncher```를 ```lateinit``` 전역변수로 두어 onCreate()에서 초기화를 먼저시키고 사용할 메소드 안에서 변수를 사용하여 해결


## 전자액자
저장소 접근 권한을 이용하여 로컬 사진을 로드할 수 있음.  
추가한 사진들을 일정한 간격으로 전환하여 보여줄 수 있음.