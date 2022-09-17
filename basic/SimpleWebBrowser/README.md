Simple Web Browser
===

## Constraint Layout
> 이전과 같이 사용방법은 동일 (constraint 조건)
> 추가사항
> > view에 constraint를 걸어 위치를 조정할 수 있는데,  
> > 이때, constraint 위치로 뷰를 완전히 배치할 수 있다면, view의 ```width```, ```height```는  
> > **0dp**로 지정해도 무방함  
> > +``constraintDimensionRatio``: 뷰 크기 - 가로/세로 비율 지정한다. ex)1:1 가로세로비율이 1:1
```xml
    <WebView
        android:id="@+id/webView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintDimensionRatio="1:2"
        app:layout_constraintTop_toBottomOf="@id/toolBar"/>
```

## SwipeRefresh Layout
> 새로고침을 편리하게 하기위한 androidx 레이아웃이다.
> ```bulid.gradle(app)```에 ```implementation 'androidx.swiperefreshlayout:swiperefreshlayout:$version```을  
> 추가하여 사용할 수 있다.
>
```kotlin
refreshLayout.setOnRefreshListener {
      // refresh 할 때 수행할 동작 정의
      ...
}


// 새로고침 후 isRefreshing 을 false로 변경해야 새로고침 아이콘이 사라진다.
refreshLayout.isRefreshing = false
```

## contentLoadingProgressBar
>  특정 뷰가 표시되지전에 최소시간을 기다리는 progress bar를 구현한다.  
> 이벤트를 완료하는데 시간이 많이 걸릴수 있는경우,UI에서 "깜빡임"을 방지하기 위해서 최소 진행률이 표시된다.

## EditText
> text를 입력 받을 때 사용하는 뷰 컴포넌트이다.
>
> ``` autoText```:   텍스트 입력 시, 자동 오타 수정 기능 사용  
> ```digits``` : EditText에 입력 가능한 문자 제한  
> ```capitalize```: 알파벳 소문자 입력(표시) 시, 대문자로 자동 변환  
> ```drawableBottom``` : 텍스트를 기준으로 아래쪽에 이미지 출력  
> ```ellipsize``` : 텍스트 생략기호(...) 또는 텍스트 흐르는 효과 주기  
> ```maxLength```: Text의 텍스트 최대 길이 제한  
> ```inputType```: 입력될 수 있는 값을 지정  
>  ```singleLine```: 개행 방지   
> **```imeOptions```**:  text입력 후 enter시 키보드의 action을 설정한다.
> ...  
> 등 다양한 속성이 존재한다.
```xml
<EditText
        android:id="@+id/addressBar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:importantForAutofill="no"
        android:imeOptions="actionDone"
        tools:ignore="LabelFor" 
        android:inputType="textUri" />

```


## WebView
> 안드로이드 앱에서 사용할 수 있는 인터넷 브라우저이다.  
> 즉, 앱안에 웹브라우저를 넣는것이다.  주로, 앱 안에서 웹페이지를 보기위하여, 또는 하이브리드 앱을 구축할 때 사용한다.


> 기초 사용법   
> 1.퍼미션 추가하기  
> 2.웹뷰 객체 생성 및 띄우기
>
```xml
<!-- 1. 인터넷 사용을 위한 internet 권한 추가 -->
<uses-permission android:name="android.permission.INTERNET"/>
```
```kotlin
// 2. 웹뷰 객체 생성 및 띄우기
webView.apply {
    webViewClient = WebViewClient()
    webChromeClient = WebChromeClient()
    settings.javaScriptEnabled = true // 자바스크립트 허용여부
    loadUrl(DEFAULT_URL)
}
```

### webView에서의 ```webViewClient```와 ```webChromeClient```
> 단순 webView에서는 화면만 보일 뿐 웹뷰속 버튼이나 다른 기능들은 동작하지 못한다.
> 이때, webView내의 기능 조작을 위해 필요한 구현체(클래스)가 **WebViwClient**와 **webChromeClient**이다.

> *webViewClient*는 주로 페이지를 로딩할 때 생기는 콜백함수들로 구성되어 있다.  
> 즉, 페이지 내부 조작이 아닌 로딩 관련한 이벤트를 조작할 수 있다.
>
> *webChromeClient*는 주로 웹페이지 내에서 일어나는 동작들에 관한 콜백함수로 구성되어 있다.



> *WebViewClient* 에서 자주 사용되는 콜백 함수
> > 1.```onPageStarted```: 페이지가 로딩되는 첫 시점에 호출된다.
> > 2.```onPageFinished```: 페이지 로딩이 끝나면 호출된다.


> *WebChromeClient*에서 자주 사용되는 콜백함수
> > 1.```onProgressChanged```: 페이지가 로딩되는 동안 호출된다.
> > 2.```onCreateWindow```: 새 창을 열 때 호출된다.
> > 3.```onCloseWindow```: 웹뷰가 창을 닫을 때 호출된다.

 ```kotlin
//  in this project example

inner class WebViewClient : android.webkit.WebViewClient() {
    // page가 시작 될 때 페이지 로딩 프로그레스바를 보여주기 시작한다.
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        progressBar.show()
}
    // page로딩이 끝나면 프로그래스 바를 감추고, url을 text로 보여준다.
    override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
            progressBar.hide()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        // 프로그래스 상태가 바뀔 때 마다, 프로그래스 바의 진행정도를 계속 보여준다(로딩 시각화)
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
        }
    }
```