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

다른 클래스에서 public 을 사용함으로써 의존성 규칙을 위반하게 된다.

접근 제한자 방식과 컴파일 후 체크 방식을 사용해서 의존성 규칙을 위반하지 않게 하기

DependencyRuleTests 클래스 참고

ArchUnitAPI 를 사용한 계층간 의존성 체크시 주의할 점은 오타를 내버리면 체크가 제대로 되지 않는 다는 점임 




