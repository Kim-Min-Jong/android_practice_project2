계산기
===
## Layout을 그리는법
+ TabLayout 사용하기
```
주로 격자(테이블)구조 layout을 구성할 때 많이 사용됨
<TabLayout
    android:shrinkColumns="*" 
    >
    ...
    <TableRow>
        ...
        <Button>
        ...
    </TableRow>
    ...
</TableLayout>

Table Layout 안에 Row를 두고, 그 안에 컴포넌트를 두어 격자 구조로 사용
컴포넌트를 넣다보면 컴포넌트의 기본 크기 때문에 layout 범위를 넘어가는 경우가 있는데,
이때 shrinkColumns="*" 를 지정해 주면 TableLayout의 width에 자동을 맞춰짐 
```
+ Constraint Layout 사용하기
+ LayoutInflater 사용하기
> layoutinflater는 미리 정의해둔 layout(.xml)을 메모리에 올려 사용할 수 있게 해주는 역할을한다.  
> xml로 이루어진 리소스를 view객체로 만들어 주는 것이다.  
> ```activity```같은 경우 ```onCreate의 setContentView```로 View를 보여주는데
> ```RecyclerView```나 ```ListView```, ```SrollView```등의 내부목록컴포넌트 등은 전용메서드가 없다.  
> 이때, row가 될 내부목록 리소스를 만들어주고 ```layoutinflater```를 통해 내부목록 view를 생성해줄 수 있다.
```
ex)
LayoutInflater.from(context).inflate(R.layout.XXX, null, false)

미리 정의되어있는 LayoutInflater객체에 context를 주어 LayoutInflater를 만들고
inflate(resource, root, attachToRoot)메소드를 통해 view 객체를 생성한다.

```
## Thread 사용하기
+ 다른 Thread 만들어서 사용하기
```
기본적으로 안드로이드는 UI Thread(Main Thread)에서 동작한다.
안드로이드 프로그램은 UI 동작 뿐아니라 네트워킹, DB등 다른 여러가지 동작을 수행하는 경우가 많다.
이떄, 네트워킹, DB등 무거운 작업들이 있는데, 이 작업들을 UI Thread에서 실행하게 되면, 작업이 끝날때 까지
UI가 멈추거나 버벅거리게 보이는 UI blocking이 발생한다. 
모바일 앱의 경우 사용자 경험을 중요하게 여겨, 사용자가 쓰기 편하게끔 만들어져야 하는데, UI blocking이 발생하면
불편한 경험을 야기하게 된다.
그렇기 때문에 UI Thread에서는 UI 관련 작업만 하게 하고, 네트워킹이나 기타 무거운 작업들은 새로운 스레드를 만들어서 
그 곳에서 처리를 해야 바람직하다.
```
```
ex) MainActivity안의 DB작업

Thread(Runnable{
      db.historyDao().insertHistory(History(null, exprText, resultText))
}).start()
```

+ runOnUiThread 사용하기
```
네트워크, DB작업등 무거운 작업들은 새로운 스레드에서 실행한다고 하였는데,
이때, 작업을 하면서 처리하거나 가져온 데이터를 view에 적용하고 싶은 경우도 있을 것이다.
하지만, 안드로이드의 view작업은 UI Thread에서 진행되어야한다.(그렇지 않을 경우 오류 발생)
이 문제를 해결하기위하여 runOnUiThread를 사용할 수 있다.
기본적으로 runOnUiThread는 지금 작업을 수행하는 쓰레드가 UI Thread라면 즉시 수행하고,
그렇지 않다면 Thread Event Queue에 적재후 추후에 실행된다.
결론적으로, runOnUiThread블록 안에서 실행되는 작업은 UI Thread에서 실행된다.
```
```
ex) 계산기 앱에서 db 기록을 가져오는 코드
Thread(Runnable{
            db.historyDao().getAll().reversed().forEach {
                // binding은 UI이 이므로 UI 쓰레드에서 실행해야함
                runOnUiThread {
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row,null, false)
                    historyView.findViewById<TextView>(R.id.expresstion_tv).text = it.expression
                    historyView.findViewById<TextView>(R.id.result_tv).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()
```
## Room 사용하기
> Room은 안드로이드 jetpack의 구성요소 중 하나이다.  
> Room 라이브러리는 SQLite를 완벽히 활용하면서 원활한 데이터베이스 액세스가 가능하도록 SQLite에 추상화 계층을 제공한다.
```
장점)
SQL 쿼리의 컴파일 시간 확인
반복적이고 오류가 발생하기 쉬운 상용구 코드를 최소화하는 편의 주석
간소화된 데이터베이스 이전 경로
출처: 안드로이드 공식 홈페이지 [https://developer.android.com/training/data-storage/room?hl=ko]
```
```
기본 사용법)
1. 먼저 앱 수준의 build.gradle에 종속성을 추가한다.
dependencies {
    ...
    implementation 'androidx.room:room-runtime:2.4.3'
    kapt 'androidx.room:room-compiler:2.4.3'
    // 버전은 달라질 수 있음
}

2. Dao, RoomDatabase, data class 등을 활용하여 사용한다.
```

> 기본 구성 요소  
>![](https://developer.android.com/static/images/training/data-storage/room_architecture.png?hl=ko)
> **database class**: 데이터베이스를 보유하고 앱의 영구 데이터와의 기본 연결을 위한 기본 액세스 포인트 역할을 한다.  
> **entity(data class)**: 앱 데이터베이스의 테이블을 나타낸다.  
> **data access object(dao)**: 앱이 데이터베이스의 데이터를 쿼리, 업데이트, 삽입, 삭제하는 데 사용할 수 있는 메서드를 제공한다.  
> 예제를 남겨두고 추후 다시 설명시 부가 설명을 진행한다.

```
이 프로젝트에 사용된 Room Object
```
```
database

@Database(entities=[History::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
```
```
entity(data class)

@Entity
data class History(
    @PrimaryKey
    val uid: Int?,
    @ColumnInfo(name="expression")
    val expression: String?,
    @ColumnInfo(name="result")
    val result: String?
)
```
```
Dao

@Dao
interface HistoryDao{

    @Query("select * from history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("delete from history")
    fun deleteAll()
}
```


## Kotlin 문법
+ 확장함수 사용하기
> 확장함수는 기존에 정의되어있는 클래스에 함수를 추가하는 기능이다.  
> 클래스별 기본 제공함수들이 있으나 본인이 필요한 클래스 함수가 있을 시 정의해서 사용할 수 있다.  
> 확장함수는 ```fun 클래스이름.함수이름(인자타입):리턴타입 { 구현 } ``` 으로 정의할 수 있다.
```
ex) String의 값이 정수값인지 확인할 수 있는 확장함수 예제이다.
    fun String.isNumber():Boolean{
        return try{
            this.toBigInteger()
            true
        } catch(e: Exception){
            false
        }
    }
```
+ data class 사용하기
> data class는 data(model)을 다루는데 최적화된 클래스이다.
> 내부적으로 ```equals(), hashcode(), toString(), copy(), componentN()```을 자동으로 생성해준다.
> 자바에서는 위 함수들을 따로 생성해주어야 했지만 코틀린에서는 data class를 사용함으로써 추가 생성작업을 줄일 수 있다.  
> 또, ```getter, setter``` 같은 경우도 자바에서는 ```getXXX(), setXXX()``` 형식으로 만들어 주어야 했지만
> data class에서는 생성자에 ```val, var``` 형식으로 선언을 해주면 자동적으로 getter,setter가 셍성된다
```
ex) java

public class History {
    private int uid;
    private String expression;
    private String result;

    public History(int uid, String expression, String result) {
        this.uid = uid;
        this.expression = expression;
        this.result = result
    }

    public History copy(History history) {
        return new History(history.uid, history.expresstion, history.result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        History that = (History) o;
        return uid == that.uid && Objects.equals(expression, that.expression) && Objects.equals(result, that.result);
    }

    @Override
    public String toString() {
        return "History{" + "expression='" + expression + '\'' + ", result=" + result + '}';
    }

}
```
```
ex) kotlin

data class history(
    val uid: Int?,
    val expresstion: String?,
    val result: String?
)
```
## 계산기
계산기 기능 수행  
계산 기록 저장하기  
계산 기록 삭제하기