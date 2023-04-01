## 아키텍처 경계 강제하기

아키텍처 경계를 강제한다는 것은 의존성이 올바른 방향을 향하도록 강제한다는 것을 의미한다.

(헥사고날 아키텍처에서 모든 의존성은 어플리케이션 코어 도메인으로 향한다)

## 접근 제한자

```
private - 동일 클래스에서 사용 가능 (같은 멤버끼리) 
default - 동일 패키지의 모든 클래스
protected - 동일 패키지의 모든 클래스 + 다른 클래스의 자식 클래스
public - 동일 패키지의 모든 클래스 + 다른 패키지의 모든 클래스
```

```
buckpal
-account
  -adpater
    -in
      -web
        - Priv AccountController // 외부 접근이 없다.
    -out
      -persistence
        - Priv AccountPersistenceAdapter
        - Priv SpringDataAccountRepository
  -domain
    - PUB Account // 다른 곳에서 호출된다.
    - PUB Activity
  -application
    - Priv SendMoneyService
    -port
      -in
        - PUB SendMoneyUseCase // 인커밍 어댑터에서 사용한다
      -out
        - PUB LoadAccountPort
        - PUB UpdateAccountStatePort // 아웃고잉 어댑터에서 구현하고 유스케이스에서 사용된다.
```

어댑터는 외부에서 접근할 필요가 없기 때문에 package-private 으로 만든다.

응용 서비스의 유스케이스 구현체 또한 인커밍 포트를 통해 호출되기 때문에 package-private 으로 만들어서 외부 접근을

차단한다.

그외 접근이 필요한 계층은 public 을 사용해서 접근 가능한 상태로 만듦.

## 컴파일 후 체크

클래스에서 public 제한자를 사용하면 아키텍처 상의 의존성 방향이 잘못되더라도 컴파일러는 잘못된 것인지 모른다.

(Account 도메인이 반대 방향을 의존해도 컴파일은 정상적으로 진행된다.)

다른 클래스에서 public 을 사용함으로써 의존성 규칙을 위반하게 된다.

접근 제한자 방식과 컴파일 후 체크 방식을 사용해서 의존성 규칙을 위반하지 않게 하기

test - DependencyRuleTests, archunit 참고 (직접 쳐봐야 이해하는 것이 빠르다!)

ArchUnitAPI 를 사용한 계층간 의존성 체크시 주의할 점은 오타를 내버리면 체크가 제대로 되지 않는 다는 점! (조심)

## 빌드 아티팩트

메이븐, 그레이들 같은 빌드 도구의 주요 기능 중 하나는 의존성 해결이다 어떤 코드베이스를 빌드 아티팩트로 변환하기

위해 빌드 도구가 가장 먼저 할 일은 코드 베이스가 의존하고 있는 모든 아티팩트가 사용 가능한지 확인하는 것이다.

패키지별 분리된 빌드 모듈(JAR) 을 만들 때 빌드 스크립트에서 아키텍처에서 허용하는 의존성을 지정해서 의존성을 체크하자! 


* 3 계층으로 나누기
```
configuration.jar 
(컴포넌트 팩토리)

web-adapter.jar // persistence-adapter.jar // etc-adapter.jar
(웹, 영속성 어댑터, 기타 어댑터) 

application.jar
(서비스, 포트, 도메인)
```
설정정보에서 빈을 만들고 어댑터와 응용 서비스를 의존한다.

어댑터 계층도 세분화 하는 것이 좋다 서로 어댑터의 의존관계가 섞여버리면 서로 영향을 미칠 수 있다.

* 애플리케이션 모듈 쪼개기
```
configuration.jar 
(컴포넌트 팩토리)

web-adapter.jar // persistence-adapter.jar // etc-adapter.jar
(웹, 영속성 어댑터, 기타 어댑터) 

api.jar
(port)

application.jar
(서비스, 도메인)
```
도메인이 포트에서 전송 객체로 사용되지 않는다면 API 모듈도 분리할 수 있다.

어댑터와 애플리케이션은 API 에 접근할 수 있지만 반대는 불가능하다.











## 결론

소프트웨어 아키텍처를 관리하지 않으면하나의 진흙덩어리가 된다. 아키텍처 구조를 잘 유지해 나가고 싶다면

의존성이 올바른 방향을 가리키고 있는지 지속적인 확인이 필요하다

접근 제한자를 사용해서 접근을 제한한다, 패키지 구조가 허용하지 않아 package-private 제한자를 사용할 수 없다면

ArchUnit 같은 컴파일 후 체크 도구를 이용하자, 아키텍처가 안정적이라고 느껴지면 아키텍처 요소를 

독립적인 빌드 모듈로 추출해야 한다. 그래야 의존성을 분명하게 제어할 수 있다.

앞서 설명한 3 가지 접근 방식 모두를 함께 조합해서 사용할 수 있다.





