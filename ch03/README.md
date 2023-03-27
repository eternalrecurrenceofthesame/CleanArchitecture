## 코드 구성하기

* 계층으로 구성하기

```
계층 패키지 구조

buckpal
-domain
  - Account
  - Activity
  - AccountRepository
  - AccountService

-persistence(infra)
  -AccountRepositoryImpl // DIP

- web
  -AccontController
```

계층 패키지 구조의 문제점!
```
1. 애플리케이션의 기능 조각이나 특성을 구분 짓는 패키지 경계가 없다. 도메인 하나에 애그리거트를 구분짓지 않고 
   넣어뒀기 때문에 User 도메인을 추가하면 기존 도메인과 섞여버린다는 의미.

2. 어떤 유스케이스들을 제공하는지 파악할 수 없다.
   AccountService 와 AccountController 가 어떤 유스케이스를 구현했는지 파악할 수 없다.
   
   * 유스케이스란? 
   행위자가 관심을 가지고 있는 유용한 일을 달성하기 위한 시나리오의 집합을 말한다.
```

* 기능으로 구성하기

'계층으로 구성하기' 방법의 문제점을 해결해보자
```
기능 패키지 구조

buckpal
-account
  -Account
  -AccountController
  -AccountRepository
  -AccountRepositoryImpl
  -SendMoneyService
```

계좌와 관련된 모든 코드들(컨트롤러,인프라,서비스)을 최상위 account 패키지에 넣어서 계층 구조 자체를 없애버렸다.

이 계층 또한 아키텍처의 가시성이 좋지 않다. 단순히 송금하기라는 유스 케이스를 구현한 것인데 이것은 단순한 

소리치는 아키텍처이다(기능을 코드를 통해 볼 수 있는 것을 말함)

기능 패키지 구조의 문제점은 OCP, DIP, DI 를 적용할 수 없고 어플리케이션 응집성을 떨어뜨려 유지보수가 힘어진다.



