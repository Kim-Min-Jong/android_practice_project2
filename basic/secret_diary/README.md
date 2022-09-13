Secret Diary
============
## Layout을 그리는 법
+ constriaint layout 사용하기
> constraint layout은 이름과 같이 뷰에 들어가는 컴포넌트에 제약조건을
> 걸어주어 뷰를 그리는 레이아웃을 말한다.
>
> view나 viewgroup에 제약을 걸어주지 않으면 오류가 발생하게 된다.
>
> 제약조건으로는 아래와 같은 것들이 있다.
>```layout_constraintLeft_toLeftOf ```
> ```layout_constraintLeft_toRightOf ```
> ```layout_constraintRight_toLeftOf  ```
> ```layout_constraintRight_toRightOf...```  
> ``` etc..```
>![출처: 안드로이드 공식 홈페이지](https://developer.android.com/static/reference/androidx/constraintlayout/widget/resources/images/relative-positioning-constraints.png?hl=ko "출처: 안드로이드 공식 홈페이지")  
> 위 사진과 같이 각 뷰마다 여러 가지 위치속성들이 있는데 top, bottom, left, right, start, end 들을
> 어디에 어떻게 조건을 주느냐에 따라 한 뷰가 다른 뷰에 제약이 걸려 뷰가 위치를 하게 된다.
>
> 이외에도 뷰의 visibility 가 gone이 되었을 때 그 뷰에 걸려 있던 다른 뷰를 어떻게 해야할지 처리하는 ```layout_goneMargin``` 속성  
> 뷰 위치의 편향을 조정하는 ```layout_constraintHori_bias``` 속성 등 여러 속성이 있다.

+ custom font 사용하기
> 앱을 만들다 보면 텍스트가 들어가게 되고 폰트도 필요하게 될 것이다.  
> 내장 폰트를 사용할 순 있지만 내장 폰트가 마음에 들지않아 외부 폰트를 가져와서 사용하고 싶은 경우도 있을 것 이다.  
> 이럴 때, 외장 폰트를 사용한다.
>> 적용법  
> > 1.ttf확장자의 폰트 파일을 다운로드 받는다.  
> > 2.안드로이드 프로젝트의 res디렉토리에 font디렉토리를 생성한다.  
> > 3.font디렉토리에 다운받은 폰트파일을 저장한다.  
> > 4.적용할 텍스트에 ```andoird:fontfamily```에 ```@font/~``` 형식으로 지정한다.

+  EditText
> 값을 입력할 때 사용하는 컴포넌트이다

## Handler 사용하기
> UI를 처리하는 Thread를 UI Thread(메인 쓰레드)라한다.   
> 별도의 작업을 하기 위해 새로운 쓰레드를 열 수 있다.    
> 만약, UI 작업을 비동기적으로 처리한다면 동기화 문제에 마주치게 된다.  
> 이러한 문제 발생을 막기 위해 병렬로 동작하는 UI 쓰레드와 작업 쓰레드 사이에 Handler를 연결하여
> 작업쓰레드에서 작업은 작업대로한 후,핸들러를 통해 message를 전달하고     
> UI 작업은 UI 쓰레드에서 실행된다.  
> 이렇게 UI 작업, 기타 작업을 나누어 실행하여 UI Thread blocking을 방지해 사용자 경험에 있어 긍정적인 효과를 줄 수 있다.   
> ex)
> ```
> # 메인 쓰레드에 연결된 handler
> private val handler = Handler(Looper.getMainLooper())
> ```
> ```
>  # runnable의 내용을 500ms 후에 실행함 (지연 실행)
>  val runnable = Runnable{ }
>  hanlder.postDelayed(runnable, 500)
> ```
## SharedPreference의 속성과 사용법
> 프로그램을 만들다보면 데이터를 다룰 일이 생기고 저장할 일이 생긴다.  
> 보통 DB를 사용을 하지만 소량의 데이터의 같은 경우엔 리소스를 많이 잡는 DB를 사용할 필요성이 낮다.  
> 그럴때에 안드로이드에서 제공하는 sharedPreference를 이용하여 key-value형식으로 데이터를 저장할 수 있다.
>> ```getSharedPreferences()```: 이름으로 식별되는 공유 환경설정 파일이 여러 개 필요한 경우 이 메서드를 사용한다. 앱의 모든 ```Context```에서 이 메서드를 호출할 수 있다.  
>> ex)
>> ``` val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)```
>>
>> 값 쓰기  
>> ```putInt()``` ```putString()``` 등을 사용하여 값을 쓸 수 있다.  
>> ex)
>> ```
>> val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
>>       with (sharedPref.edit()) {
>>          putInt(getString(R.string.saved_high_score_key), newHighScore)
>>          apply()
>>      }
>> 
>> apply()는 sharedPreference 객체를 즉시 변경하지만 비동기적으로 실행된다.
>> commit()은 동기적으로 실행된다. 그렇기에, UI 쓰레드에서 호출하는 것은 피해야한다.  
>> UI가 멈출 수 있기 때문이다.(동작이 크면 클수록 멈춰있는 시간이 길어짐 - UI thread blocking)
>> ``` 
## Theme 사용하기
> theme은 개별 뷰뿐만아니라 앱, 액티비티 등 보이는 뷰에 전체적으로 적용되는 속성 모음이다.  
> theme을 적용하면 뷰에서 지원하는 테마 속성이 모든 뷰에 적용이 된다.  
> theme은 ```res/values/themes.xml``` 에서 설정할 수 있다.(커스텀 가능)
>
>> (주의) Button, TextView 등 기본 컴포넌트들을 보면 머터리얼 속성이 기본으로 적용되어있어
>> 뷰의 색 등 속성을 바꿀 시 적용이 안되는 문제가 생길 수 있다.  
>> 이때 기본 컴포넌트말고 ```androidx.appcompat.widget```의 컴포넌트를 사용하면 해결할 수 있다.

## AlertDialog 사용하기
> AlerDialog 클래스의 Builder를 통해 생성할 수 있다.  
> ```title, Message, PositiveButton, NegetiveButton, View``` 등 다양한 속성을 지정하여 dialog를 만들 수 있다.  
> ex)
>```     
>        AlertDialog.Builder(this)
>              .setTitle("실패")
>              .setMessage("비밀번호가 잘못되었습니다.")
>              .setPositiveButton("확인"){_, _ ->}
>              .create().show()
>```


# Kotlin 문법
> lambda식에서 사용하지 않는 파라미터 _로 처리하기  
> ex)
> ```
> .setPositiveButton(""){ dialog, which ->
> 
> }
> 
> -->
> 
> .setPositiveButton(""){ _, _ ->
> 
> }
> ```
# 비밀 다이어리
+ 다이어리처럼 UI 꾸며보기
+ 비밀번호를 저장하는 기능, 변경하는 기능
+ 다이어리 내용을 앱이 종료되어도 기기에 저장하는 기능
