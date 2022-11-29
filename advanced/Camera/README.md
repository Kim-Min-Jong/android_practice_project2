Camera app
===

## CameraX [공식문서](https://developer.android.com/training/camerax)
>CameraX는 더 쉬운 카메라 앱 개발을 위해 빌드된 Jetpack 라이브러리이다.  
> 새 앱을 개발할 때는 CameraX로 시작하는 것이 좋다.  
> CameraX는 대부분의 Android 기기에서 작동하며 이전 버전인 Android 5.0(API 수준 21)까지 호환되는 일관되고 사용하기 쉬운 API를 제공한다.

### 장점
+ 광범위한 기기 호환성
    - CameraX는 Android 5.0(API 수준 21) 이상을 실행하는 기기(기존 Android 기기의 98% 이상)를 지원한다.


+ 사용 편의성
    - CameraX에서는 기기별 차이를 관리하는 대신 실행해야 하는 작업에만 집중할 수 있도록 지원하는 사용 사례가 도입되었다. 다음과 같은 대부분의 일반적인 카메라 사용 사례가 지원된다.
        * [미리보기](https://developer.android.com/training/camerax/preview) : 화면에서 이미지를 봅니다.
        * [이미지 분석](https://developer.android.com/training/camerax/analyze) : ML Kit로 전달하는 경우와 같이 알고리즘에 사용할 수 있도록 버퍼에 원활하게 액세스합니다.
        * [이미지 캡처](https://developer.android.com/training/camerax/take-photo) : 이미지를 저장합니다.
        * [동영상 캡처](https://developer.android.com/training/camerax/video-capture) : 동영상과 오디오를 저장합니다.


+ 기기 간 일관성
    - 카메라 동작을 일관되게 유지하기란 쉽지 않은 일이다.   가로세로 비율, 방향, 회전, 미리보기 크기, 이미지 크기를 고려해야 한다.   CameraX를 사용하면 이러한 기본적인 동작이 자동으로 해결된다.


+ 카메라 확장 프로그램
    - CameraX에는 적게는 단 두 줄의 코드로 기기의 기본 카메라 앱과 동일한 기능에 액세스할 수 있게 해 주는 선택적 [Extensions API](https://developer.android.com/training/camerax/extensions-api) 가 있다.
    - 확장 프로그램으로는 빛망울 효과(세로 모드), HDR(High Dynamic Range), 야간 모드, 얼굴 보정 등이 있다(모두 기기 지원이 필요함).



### CameraX 세팅
1. **CameraXConfig.Provider**를 구성한다.
2. 레아아웃에 미리보기 화면 뷰 **androidx.camera.view.PreviewView**를 추가한다.
3. **CameraProvider**를 요청한다.
4. View를 만들 때 **CameraProvider**를 확인한다.
5. 카메라를 선택하고 수명 주기 및 UseCases를 결합한다.


### 촬영 후 저장 및 미리보기 구현과정
1. **캡쳐 버튼**에 리스너를 등록한다.
2. 촬영하여 **저장할 파일**을 선언한다.
3. 사진 촬영 및 **저장 콜백을 구현**한다.
4. 사진을 저장한 **Uri를 Broadcast하여** 다른 갤러리에 보이도록 한다.
5. 저장한 사진에 대해 최신 사진의 미리보기를 구현한다.

```kotlin
    // 카메라 캡쳐하기
private fun captureCamera() {
    // 앱이 실행되어 이미지 캡쳐 객체가 있어야하는데 없으면 바로 리턴
    if (::imageCapture.isInitialized.not()) return

    // 캡쳐 저장 시작
    // 파일 선언
    val photoFile = File(
        PathUtil.getOutputDirectory(this),
        SimpleDateFormat(
            FILENAME_FORMAT, Locale.KOREA
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    // 파일을 쓸 수 있는 옵션 지정 (ImageCapture)
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    // 플래시 체크시 켜기
    if (isFlashEnabled)
        flashLight(true)
    // 이미지 캡쳐를 캡쳐함 (사진 찍기)  - 찍고 저장될 떄의 콜백을 지정
    imageCapture.takePicture(
        outputFileOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                contentUri = savedUri
                updateSavedImageContent()
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                isCapturing = false
                flashLight(false)
            }

        })

    // 플래시 제어
    private fun flashLight(light: Boolean) {
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        if (hasFlash) {
            // cameraControl의 enableTorch(Boolean) 메소드를 통해 플래시를 키고 끔
            camera?.cameraControl?.enableTorch(light)
        }
    }

```

## ViewPager2 및 CircleIndicator
recycler view와 사용방법이 거의 동일  
[ViewPager2 복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/TodayQuotes#view-pager2)

### Circle Indicator for ViewPager
> ViewPager 하단에 현재 페이지의 index를 표시하는 인디케이터이다.
>
> 외부라이브러리이므로 앱 수준의 의존성 추가를 해야한다.  
> ```implementation 'me.relex:circleindicator:2.1.6'```
>
> 예시  
![](https://github.com/ongakuer/CircleIndicator/blob/master/screenshot.gif?raw=true)


뷰 선언
```xml
    <me.relex.circleindicator.CircleIndicator3
        android:id="@+id/indicator"
        android:layout_width="0dp"
        android:layout_height="48dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>
```

제어
```kotlin
// indicaotor 컴포넌트에 viewPager를 넣어주면 자동적으로 세팅이 된다.
binding?.indicator?.setViewPager(binding?.imageViewPager)
```




기타 자세한 것은 코드에 주석을 보면서 복습..  