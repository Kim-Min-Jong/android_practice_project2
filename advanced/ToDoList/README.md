ToDo List
---

## Architecture
+ 아키텍쳐를 사용하는 이유
    - 일관적인 코드작성 (유지보수, 협업 능률 상승)
    - 생산성 향상
    - 테스트의 용이성
    - 어플리케이션의 개발 방향성을 잡아줌 (동일한 목표)


+ 아키텍쳐의 종류
    - *MVC*: model + view + controlller
    - *MVP*: model + view(viewController) + presenter
    - *MVVM*: model + view(viewController) + viewmodel
    - *MVVM* + *DataBinding*
    - *MVI*: model + view + intent
    - *MvRx*(framework-airbnb), *Flux*(framework-facebook), *Ribs*, etc....


### MVP architecture
![img_18](https://user-images.githubusercontent.com/79445881/203063503-da7871d3-2492-44c9-8bc3-a09133625652.png)

> **mvp 아키텍쳐**  
> view - presenter - model 로 분리 된 아키텍쳐이다.  
> Model과 View는 MVC 패턴과 동일하고, Controller 대신 Presenter가 존재한다.  
> presenter를 통하여 비지니스 로직을 호출한다.  
> presenter는 모델에 직접 접근하여 데이터를 가공하여 view로 전달한다.  
> view와 model을 이어주는 중재자 역할을 한다.
>
>
> *동작 과정*
> 1. 사용자의 액션은 View를 통해 들어온다.
> 2. View는 데이터를 직접 Presenter에 요청한다.
> 3. Presenter는 Model에게 직접 데이터를 요청한다.
> 4. Model은 Presenter에서 요청받은 데이터를 가공하여 응답한다.
> 5. Presenter는 View에게 데이터를 응답한다.
> 6. View는 Presenter가 응답한 데이터를 이용하여 화면에 나타낸다.
>
>
> *특징*  
> 중재자인 Presenter는 view와 model의 인스턴스를 가지고 있어 둘을 연결 할 수 있다.
> presenter와 view는 1:1로 설정한다.
>
>
> *장점*  
> MVC의 단점인 view - model 사이의 의존성을 해결하였다.
>
>
> *단점*  
> view - model 사이의 의존성은 해결되었지만, 1:1로 관계를 맺고있는 view - presenter사이의
> 의존성이 높아진다.

### MVVM architecture
![img_19](https://user-images.githubusercontent.com/79445881/203063530-2ef32155-47ba-4610-949a-7e2e9b652728.png)

> **mvvm 아키텍쳐**  
> view -viewModel - model로 분리 된 아키텍쳐이다.   
> Model과 View는 MVP 패턴과 동일하고, Presenter 대신 ViewModel이 존재한다.    
> ViewModel을 통하여 비지니스 로직을 호출한다.  
> MVC의 경우에는 안드로이드에서 적용할 때 View와 Controller가 Activity에서 모두 처리되어야하기 때문에 Activity가 커지는 문제가 있어서 관심사의 분리가 비교적 원활하지 않다고 여겨졌다.  
> MVP는 Presenter가 View와 1대1로 동작하기 때문에 View와 Presenter의 의존성이 강해지는 문제가 발생하고 이에 따라 종종 프레젠터의 로직이 무거워지는 문제가 발생하기도 했다.
> 이를 해결하기 위해여 MVVM아키텍쳐가 사용되기 시작하였다.
>
>
> *특징*  
> Model, View는 역할이 동일하고, ViewModel은 View를 표현하기 위해 만든 View를 위한 Model이다. View를 나타내 주기 위한 Model이자 View를 나타내기 위한 데이터 처리하는 역할을 한다.
> 옵저버패턴과 DataBinding을 통해 view와 viewmodel사이의 의존성을 없애주었다.
> 즉, view는 viewmodel을 호출하고 바라보기만 할 뿐이다. 이외의 참조를 하지않는다.
> view와 ViewModel은 N:1의 관계를 갖는다.
>
>
> *장점*  
> MVVM 패턴은 View와 Model 사이의 의존성이 없다. 또한 옵저버 패턴 또는 Data Binding을 사용하여 View와 View Model 사이의 의존성 또한 없앤 디자인패턴이다. 각각의 부분은 독립적이기 때문에 모듈화 하여 개발할 수 있다.
>
>
> *단점*  
> MVVM 패턴의 단점은 View Model의 설계가 어렵다.(패턴 적용)


### google architecture [문서](https://developer.android.com/jetpack/guide)
구글에서 권장하는 아키텍쳐 가이드이다.

> **일반 아키텍쳐 원칙**
> 1. 관심사 분리  
     >따라야 할 가장 중요한 원칙은 관심사 분리입니다. Activity 또는 Fragment에 모든 코드를 작성하는 실수는 흔히 일어납니다. 이러한 UI 기반의 클래스는 UI 및 운영체제 상호작용을 처리하는 로직만 포함해야 합니다. 이러한 클래스를 최대한 가볍게 유지하여 구성요소 수명 주기와 관련된 많은 문제를 피하고 그러한 클래스의 테스트 가능성을 개선할 수 있습니다.
     > Activity 및 Fragment 구현은 소유 대상이 아니며 Android OS와 앱 사이의 계약을 나타내도록 이어주는 클래스일 뿐입니다. OS는 사용자 상호작용을 기반으로 또는 메모리 부족과 같은 시스템 조건으로 인해 언제든지 클래스를 제거할 수 있습니다. 만족스러운 사용자 환경과 더욱 수월한 앱 관리 환경을 제공하려면 이러한 클래스에 대한 의존성을 최소화하는 것이 좋습니다.
>
> 2. 데이터 모델에서 UI 도출하기  
     > 또 하나의 중요한 원칙은 데이터 모델에서 UI를 도출해야 한다는 것입니다. 가급적 지속적인 모델을 권장합니다. 데이터 모델은 앱의 데이터를 나타냅니다. 이들은 앱의 UI 요소 및 기타 구성요소와 독립되어 있습니다. 즉, 이들은 UI 및 앱 구성요소 수명 주기와는 관련이 없습니다. 하지만 OS에서 메모리에서 앱의 프로세스를 삭제하기로 결정하면 여전히 삭제됩니다.
     > 지속 모델이 이상적인 이유는 다음과 같습니다.
     > Android OS에서 리소스를 확보하기 위해 앱을 제거해도 사용자 데이터가 삭제되지 않습니다.
     > 네트워크 연결이 취약하거나 연결되어 있지 않아도 앱이 계속 작동합니다.
     > 앱 아키텍처를 데이터 모델 클래스에 기반하는 경우 앱의 테스트 가능성과 견고성이 더 높아집니다.
     ![img_20](https://user-images.githubusercontent.com/79445881/203063539-f67bea5a-bb51-49f2-8bc6-fc2b4864caa3.png)



## DI vs Service Locator
> *Dependency Injection*
> 컴포넌트간의 의존 관계를 소스코드 내부가 아닌 외부 설정 파일등을 통해
> 정의되게하는 디자인 패턴 중 하나 이다.
>
> 객체를 직접 생성하지 않고 외부에서 주입한 객체를 사용하는 방식이다.
>
> 인스턴스간 결합도를 낮춰준다 -> 유닛테스트 용이성을 증대시킨다.
> ex) hilt, dagger
>

> *Service Locator*  
> 중앙 등록자인 'service locator'를 통해 요청이 들어왔을 때 특정 인스턴스를 반환해주는 형식이다.  
> apk크기, 빌드 속도, 메서드 수 등 복잡한 제약이 있는 경우 사용하기 편하다.
>
> ex) *Koin*(경량화 된 DI라고 소개하지만, 내부동작은 service locator로 봐도 무방하다.)

![img_21](https://user-images.githubusercontent.com/79445881/203063548-3ace3dfb-0422-4a69-8a1f-6f2a98e82ba5.png)

### Koin
> Kotlin DSL로 만들어졌으며, Dagger에 비해 구성요소가 복잡하지 않아서 러닝커브가 났다.
>
> 단, Koin은 리플렉션을 이용해 런타임에 의존성 주입을 하다보니 앱성능이 저하된다는 단점이 있다.  
> 그래서 앱 규모가 커지면, app이 시작할 때 의존성 주입이 시작되니, 화면이 멈춘것처럼 보일 수 있다.
> 그래서 앱 규모가 크면, Dagger-hilt를 이용해 의존성 주입을 하는 것이 좋다.  
> 물론, hilt도 컴파일 시 의존성 주입을 하기 때문에 컴파일 시간이 길어지는 단점이 있다.
>
> 결론적으로, Koin은 작은 규모의 프로젝트에 의존성 주입을 빠르게 적용하고 싶을 때 추천된다.

> *주요 컴포넌트*  
> ```module{ ... }```: 키워드로 주입받고자하는 **객체의 집합**  
> ```single{ ... }```: 앱이 실행되는 동안 계속 유지되는 **싱글톤 객체 생성**
> ```factory{ ... }```: 요청할 때마다 매번 **새로운 객체를 생성**  
> ```get()```: 컴포넌트 내에서 **알맞은 의존성을 주입**
>
>
> **장점**
> - 러닝커브가 낮아 쉽고 빠르게 DI를 적용할 수 있다.
> - Kotlin 개발 환경에 도입하기 쉽다.
> - 별도의 어노테이션을 사용하지 않기 때문에 컴파일 시간이 단축된다.
> - ViewModel 주입을 쉽게 할 수 있는 별도의 라이브러리를 제공한다.
>
>
> **단점**
> - 런타임시 주입이 필요한 컴포넌트가 생성 되어있지 않는 파라미터가 있는 경우, 크래시가 발생한다.
> - 컴파일 타임에 주입 대상을 선정하는 DI에 비해 런타임에 서비스 로케이팅을 통해 인스턴스를 동적으로 주입해주기 때문에 런타임 퍼포먼스가 떨어진다.





## TDD base on scenario
> **TDD**: **Test Driven Development**의 약자로, 테스트가 개발을 이끌어감을 의미  
> 테스트를 먼저 만들고 테스트를 통과하기 위한 것을 짜는 것을 의미한다.  
> 결정관 피드백 사이에 Gap을 조절할 수 있는 테크닉 중 하나이다.

> *TDD가 필요한 이유*
> 1. 에자일과 같이 매우 빠르고 많은 프로덕트 개선이 일어나는 과정에선 어쩔 수 없는 불확실성을 따름
> 2. 이러한 이유로 빠른 커뮤니케이션 핑퐁, 피드백과 협력이 필요할 수 밖에 없음
> 3. TDD도 마찬가지로 잦은 피드백과 협력을 증진 시킬 수 있는 방법이기에, 1번과 같은 상황에서 충분히 도움이됨

> *TDD가 필요한 상황*
> 1. 결과가 너무나도 뻔하다면, 굳이 TDD를 기반으로 개발하지 않아도 된다.
> 2. 자신감 지수가 낮은경우(처음 시도해보거나, 불확실성이 높은경우)
> 3. 요구사항이 빈번하게 변경되는 경우
     >   1. 외부적인 요인으로 인해 잦은 주기로 바뀌는 스펙에 대응하기 좋음
> 4. 테크니컬한 스펙, 비즈니스 로직이 자주 바뀌는 경우
> 5. 개발하고 난 이후에 다른 개발자에게 해당 스펙에 대해 인수인계가 필요한 경우

> *TDD의 단점(어려운점)*
>  - 개발 시간의 증가 -> 필요성을 못느끼는 경우가 있음
>  - TDD자체가 어렵다.
     >    * 일반적인 개발방식과 반대로 가져가야함 from(개발->테스트) to (테스트->개발)
>    * 체득한 것을 내려놓기 어려움(기존의 개발방식)
> - TDD를 하기위해 '프레임워크, 툴을 사용해야한다'라는 관념이 있어 진입장벽을 만든다.
    >   * 이런 규칙에 얽매이게되면 민첩한 방식으로 개발을 주도해가기 어렵다.
>   * 도구/규칙에 집착하게 되면 오히려 TDD가 어렵다.

> *TDD를 잘 하는 방법*
> - 피드백을 자주 받을 수 있는 환경을 만든다.
    >   * Test Case 작성
>   * 주기적인 프로세스 검증
>
> - 내가 하는 작업에 협력을 할 수 있는 방법으로 유도한다.
    >   * 함께 비즈니스 로직을 고민
>   * 프로덕트 스펙을 잘게 쪼갠다. --> Test Case가 다양해진다.
>   * 민첩하게 비즈니스 로직을 구현 --> 빠르게 검증 가능


>  *TDD 구현 과정*
> 1. 비즈니스 로직을 분리할 수 있는 클린 아키텍쳐 Base를 구축
> 2. Unit Test 구현을 위한 도구 도입(DI, Mock 도입)
> 3. 각 Unit Test File 생성 및 목적에 맞는 시나리오(비즈니스로직) 작성하기
> 4. 시나리오에 필요한 UseCase 작성, 각 레이어 구축(Repository, Model 등)
> 5. 각 상태를 State Pattern으로 표현하여 쉬운 방법을 결과 검증하기



## Todo List

> **Repository pattern**
>
> 데이터 출처(로컬 DB인지 API응답인지 등)와 관계 없이 동일 인터페이스로 데이터에 접속할 수 있도록 만드는 것을 Repository 패턴이라고 한다.  
> (일단 todolist에는 api call은 없지만 아키텍쳐 구성을위해 패턴 적용)
> ![img_23](https://user-images.githubusercontent.com/79445881/203063565-9972a03c-4aeb-4a95-aa3b-d7eda60067dd.png)
> - viewModel 밑에 Repository라는 layer를 하나 더 두어서 viewModel은 오직 비즈니스 로직만 집중하게 한다.
    >
    >  (데이터를 로컬과 서버 중 어디서 가져올지, 또 어떻게 가공할지는 Repostitory가 하기 때문다.)
>- viewModel들간 Repository를 공유해서 데이터 일관성을 유지할 수 있습니다.


> **아키텍쳐 설계** (클린 아키텍쳐 + MVP)  
> ![img_24](https://user-images.githubusercontent.com/79445881/203063579-d04cb504-3744-4803-a6b5-ac2ccd82620d.png)
> - Presentation Layer
    >   * View - Activity, Fragment - 보여질 뷰
>   * ViewModel - ListView, DetailView - 뷰에 보여줄 데이터를 가공하는 비즈니스 로직
>
> - Domain Layer
    >   * UseCase - Repository에서 가져온 함수를 viewModel에 넣어주기 위해 각각에 맞는 UseCase로 분리함
>   * Translater (Entity -> Model 변환)
>
> - Data Layer
    >   * Repository
          >     + Domain과 DataStore, Remote Layer를 연결하기 위함
>     + RoomDatabase를 통해 데이터를 가져와 Domain Layer로 전달
>   * Entity
      >     + 최소 단위의 비즈니스 객체
>
> - appModule
    >   + Koin을 통해 외부에서 객체를 만들어 주어 클래스로 객체 주입 (Dependency injection)


> **TDD**  
> ![img_22](https://user-images.githubusercontent.com/79445881/203063555-a50992fa-844c-48e3-a36a-47f75de4a026.png)
>
> 테스트를 위해 Repository, di, viewModel(test code)로 분리하여 테스트 코드를 작성
>
> ```LiveDataTestObserver```: 테스트에서 데이터 변경시 liveData를 통해 변화를 감지하여 테스트 진행하기 위해 옵저버 패턴 사용  
> ```viewmodel packge```: 각 시나리오에 맞는 비즈니스 로직을 작성하여 테스트 진행  
>```di package```: 테스트에 사용 될 Koin 객체 세팅

