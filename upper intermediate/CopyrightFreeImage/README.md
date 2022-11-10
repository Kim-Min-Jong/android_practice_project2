Copyright Free Image
===

## Unsplash API with Retrofit, Coroutine
> 저작권 무료 이미지를 제공하는 unsplash.com에서 제공하는 API를 사용하여 무료이미지 검색, 다운로드 기능을 구현하였다.
>
>

> *진행과정*
> 먼저, API 사용을 위해 [unsplash.com](unsplash.com)에서 회원가입을 진행한다.  
> 회원가입 후, [unsplash.com/developers](unsplash.com/developers)에 접속하여 로그인을 하고 상단에 있는 Your apps에  
> 가서 어플리케이션을 만든다.(약관 참조)
>
> api에 접근하려면 Access Key가 필요한데. 어플리케이션을 만들면, 앱의 Access Key가 제공된다.
> 그 키를 바탕으로 Retrofit Service를 구현한다.
>
> ```kotlin
> interface UnsplashApiService {
>   // 여기서는 무작위 사진을 가져오기 위해 random photo api를 사용하였다.
>   // 갯수는 30개로 제한하였다. (너무 많으면 느려지므로)
>     @GET(
>         "photos/random?" +
>         "client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}" +
>         "&count=30"
>     )
>     suspend fun getRandomPhotos(
>         // Optional parameter 인 query에 String? 형태의 
>         //파라미터를 제공함으로써 검색도 할 수 있고, 쿼리가 없을 시
>         // 무작위 사진을 받아 올 수 있도록 하였다.
>         @Query("query") query: String?
>     ): Response<List<PhotoResponse>>
> }
>
> ```
> ```kotlin
> object Repository {
>
>     private val unsplashApiService: UnsplashApiService by lazy {
>         Retrofit.Builder()
>             .baseUrl(Url.UNSPLAH_BASE_URL)
>             .addConverterFactory(GsonConverterFactory.create())
>               // api 통신 로그
>             .client(buildOkHttpClient())
>             .build()
>             .create()
>     }
>
>      // MainActivity에서 쓰일 실제 함수  (응답 값)
>     suspend fun getRandomPhotos(query: String?): List<PhotoResponse>? =
>         unsplashApiService.getRandomPhotos(query).body()
>
>     private fun buildOkHttpClient(): OkHttpClient =
>         OkHttpClient.Builder()
>             .addInterceptor(
>                 //인터셉터로 로그를 가져와 찍되 레벨 설정을 하여 로그내용의 수준을 결정함
>                 HttpLoggingInterceptor().apply {
>                     level = if(BuildConfig.DEBUG) {
>                         HttpLoggingInterceptor.Level.BODY
>                     } else {
>                         HttpLoggingInterceptor.Level.NONE
>                     }
>                 }
>             )
>             .build()
> }
> ```
> object(singleton) 으로 Retrofit 객체를 구현하였다.

> MainActivity에서는 이미지들을 보여 줄 Recycler View를 초기화하고, 검색기능을 위해
> EditText에서 검색을 할 수 있도록 ```setOnEditorActionListener```를 설정하여 검색 기능을 구현했다.
> 또, 이미지 API는 네트워크 작업이므로 비동기 코루틴 스코프안에서 실행하도록 하였다.

```kotlin
// Recycler View 설정
private fun initViews() {
    binding?.recyclerView?.apply {
        layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        adapter = PhotoAdapter()
    }
}

// editText Listener 설정
binding?.searchEditText?.setOnEditorActionListener { editText, actionId, keyEvent ->
    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        currentFocus?.let {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            it.clearFocus()
        }

        fetchRandomPhotos(editText.text.toString())
    }
    true
}

// API 통신
private fun fetchRandomPhotos(query: String? = null) {
    scope.launch {
        // 렌더링시 에러처리
        try {
            Repository.getRandomPhotos(query)?.let { photos ->

                binding?.errorDescriptionTextView?.visibility = View.GONE
                (binding?.recyclerView?.adapter as? PhotoAdapter)?.apply {
                    this.photos = photos
                    notifyDataSetChanged()
                }
            }
            binding?.recyclerView?.visibility = View.VISIBLE
        } catch (e: Exception) {
            binding?.recyclerView?.visibility = View.INVISIBLE
            binding?.errorDescriptionTextView?.visibility = View.VISIBLE
        } finally {
            binding?.shimmerLayout?.visibility = View.GONE
            binding?.refreshLayout?.isRefreshing = false
        }
    }
}
```





## WallPaper Manager
> 이미지나 사진을 배경화면으로 지정하고자 할 때 사용하는 안드로이드 내장클래스이다.
> setBitmap 내장함수에 bitmap을 넣어주어 배경화면으로 지정한다.
>
```kotlin
// 사진을 배경화면으로 지정하기위해 WallPaperManager 사용
val wallPaperManager = WallpaperManager.getInstance(this@MainActivity)

val snackBar = Snackbar.make(binding?.root as View, "다운로드 완료", Snackbar.LENGTH_SHORT)

if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
       wallPaperManager.isWallpaperSupported &&
             wallPaperManager.isSetWallpaperAllowed) {
                   snackBar.setAction("배경 화면으로 저장") {
                        try {
                              wallPaperManager.setBitmap(resource)
                            } catch (e: Exception) { 
                                Snackbar.make(binding?.root as View, "배경화면 저장 실패..", Snackbar.LENGTH_SHORT)
                            }
                   }
   
        }
```
> Wallpaper Manager가 동작하기 위해서는 wallpaperManager의 ```isWallpaperSupported```과  
> ```isSetWallpaperAllowed```속성을 확인해서 true여야 적용가능하다.  
> 단,여기서 ```isWallpaperSupported```는 안드로이드 M(API 23) 이상에서만 지원하고,  
> ```isSetWallpaperAllowed```는 안드로이드 N(API 24)이상에서만 지원된다.  
> 그렇기 때문에, 코드를 작성할 떄 기기의 SDK 버전이 N(API 24)이상인지 확인하는 작업도 필요하다.

## Loading Shimmer (Shimmer Layout) [공식 페이지](http://facebook.github.io/shimmer-android/)
> Loading Shimmer란 레이아웃이 로딩될 때, 그 레이아웃이 어떤 모양을 하고 있는지 개괄적으로 보여주는 것을 말한다.  
> Facebook 앱 실행시 컨텐츠에 바로 뜨지않고, 로딩때문에 잠시 기다리게되는데 이때 보여지는 것이 Loading shimmer이다.
>
>
> ![](https://k.kakaocdn.net/dn/blmfjE/btqBqpQCJUx/EM0Y3siPwYMboGJqze8N7K/img.gif)
>
> 위와 같이 로딩전 레이아웃의 위치나 모양등 개괄적인 것을 보여준다.
>
> 직접 구현하는 것은 어려운 과정이지만, facebook에서 안드로이드용 shimmer 라이브러리를 만들어 놓아서
> 편리하게 사용할 수 있다.
>

> **사용법**
>
> 먼저,외부 라이브러리이므로 앱수준 gradle에 의존성을 추가해준다.
>
> ```implementation 'com.facebook.shimmer:shimmer:0.5.0'```
>
> 그 후, 레이아웃 파일에 shimmerLayout을 정의한다.
> ```xml
>  <com.facebook.shimmer.ShimmerFrameLayout
>        android:id="@+id/shimmerLayout"
>        android:layout_width="match_parent"
>        android:layout_height="match_parent">
>
>         <LinearLayout
>               android:layout_width="match_parent"
>               android:layout_height="wrap_content"
>                android:orientation="vertical">
>               <include layout="@layout/view_shimmer_item_photo" />
>         </LinearLayout>
>
>         <!-- 등등.. 설정할 뷰 삽입 -->
>
>  </com.facebook.shimmer.ShimmerFrameLayout>
> ```
>
> Shimmer Layout의 기본설정이 auto start이므로 ```shimmer_auto_start```속성을 false로  
> 지정하지 않는 이상, Shimmer Layout은 선언만 하면 자동으로 동작한다.



## Image Download
MediaStore를 통하여, 앱이 아닌 휴대폰에 직접 이미지를 저장할 수 있는 로직이다.
```kotlin
    // scope storage로 인한 MediaStore 분기처리가 요구됨 (안드로이드 10이상 기준)
    private fun saveBitmapToMediaStore(bitmap: Bitmap) {
        // 컨텐츠 리졸버를 통하여 미디어스토어의 설정
        val fileName = "${System.currentTimeMillis()}.jpg"
        val resolver = applicationContext.contentResolver
        val imageCollectionUrl =
            //10 이상일 경우 정해진 Volume들이 있음 VOLUME_EXTERNAL_PRIMARY- 읽고 쓰기 가능
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                //10이상이 아닐 경우 그냥 외부 URI 갖고 오면 됨
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            // (10이상일때) 이미지 저장시 긴 시간이 걸릴 수 있는데, 이 사이에 그 이미지 파일에 접근할 수도 있다.
            // 이것을 IS_PENDING 값을 1로 두면 막을 수 있고, 0이 되면 그때부터 접근할 수 있다.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollectionUrl, imageDetails)
        imageUri ?: return

        // 실제 저장 과정
        resolver.openOutputStream(imageUri).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

        }

        // 다시 이미지의 접근 권한을 바꾸고 리졸버의 설정을 업데이트한다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }


    }
```
> 안드로이드 10미만은 쓰기 권한을 얻은 후 다운로드를 진행할 수 있다.
```kotlin
// 권한요청 및 결과

// requestPermissions을 통해 권한 요청
private fun requestStoragePermission() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
    )
}

// 권한을 확인한 후, 권한이 확인 되면 이미지를 가져와 로딩하고
// 이미지를 클릭시 다운로드 할 수 있음
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    val writeExternalStoragePermissionGranted =
        requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED

    if(writeExternalStoragePermissionGranted) {
        fetchRandomPhotos()
    } else {
        requestStoragePermission()
    }
}

```
