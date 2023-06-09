## 경계 간 매핑하기

* '매핑하지 않기' 전략
```
웹에서 모델을 애플리케이션에 넘겨줄 때 공통 도메인 모델(Account)을 웹과 애플리케이션에서 사용하면

도메인 모델이 웹과 애플리케이션이 사용하는 어댑터의 요구사항에 ex) Json 직렬화 애노테이션, ORM 매핑 애노테이션 등등

맞춰서 설계되고 단일 책임원칙(한 클래스는 하나의 책임을 가진다)에 위반된다.
```
```
매핑하지 않기 전략은 간단한 CRUD 에서 사용될 수 있다 ex) 모든 계층이 정확히 같은 구조, 같은 정보를 필요로 할 경우

하지만 앞서 설명한 상황이라면 다른 전략을 선택하자.
```

* '양방향' 매핑 전략
```
각 어댑터가 전용 모델을 가지고 해당 모델을 도메인 모델로, 도메인 모델을 해당 모델로 매핑할 책임을 가진다. 
(인커밍/아웃고잉 포트)

웹은 인커밍 포트에 필요한 모델로 매핑해서 애플리케이션을 호출하고 리턴 값을 웹 모델로 매핑해서 응답한다.

아웃 고잉 포트 영속성 계층은 ORM 매핑 애노테이션을 사용한 엔티티를 만들어서 매핑한다. (영속성 모델)

각 계층이 전용 모델을 가지고 있기 때문에 전용 모델을 변경하더라도 도메인에는 영향이 미치지 않는다 (오염되지 않음)

'매핑하지 않기' 전략 다음으로 간단하게 사용할 수 있다! 100p 그림 참고 
```
```
양방향 매핑 전략을 사용하면 많은 보일러 플레이트코드(반복적 코드)가 생긴다 

도메인 모델이 계층을 넘어서 통신하게 된다. 인커밍,아웃고잉 어댑터에서 도메인을 사용하면 도메인 외부 계층의 

요구에 따른 변경에 취약해질 수 있다.
```

* '완전' 매핑 전략
```
양방향 매핑 전략에서 유스케이스 전용 모델(command)이 추가된 전략.

웹 계층은 HTTP 요청을 검증하고 웹 계층에서 애플리케이션의 커멘드 객체로 매핑한다.

유스케이스가 별도의 커멘드 모델을 가짐으로써 전용 필드와 유효성 검증 로직을 가진 전용 커맨드의 역할을 수행한다.

유스케이스는 커멘드 모델을 사용한다.

이 전략은 어댑터와 애플리케이션 사이에서 유스케이스의 경계를 명확하게 할 필요가 있을 때 사용한다.

각 포트마다 유스케이스 전용 커맨드를 만들 필요는 없다. 인커밍 포트에서 커멘드로 값을 받고 아웃고잉 포트출력을 

도메인 모델로 받아도 된다. (예제 코드와 같이)

매핑 전략은 여러 가지를 섞어서 사용할 수 있고 섞어서 사용해야한다. 모든 계층에 걸쳐 전역 규칙으로 만들필요는 없다.
```

* '단방향' 매핑 전략
```
도메인 모델 인터페이스를 만들어서 각 계층에 제공할 수 있다. 이때 도메인 모델의 상태를 변경할 수 없게 setter 는 제공하지 않는다

도메인의 독립성을 유지하면서 별다른 매핑없이 사용할 수 있다. 

도메인이 인터페이스를 제공하기 때문에 인터페이스에 의존해서 순수한 도메인 모델을 사용할 수도 있고 전용 모델로 매핑해서 사용할 수도 있다.

이 전략은 계층 간의 모델이 비슷할 때 효과적이다. 읽기 전용 연산의 경우 인터페이스가 필요한 모든 정보를 제공하기 때문에

웹 계층에서 전용 모델로 매핑할 필요 없이 단방향 인터페이스를 제공할 수 있다.
```

## 언제 어떤 매핑 전략을 사용할 것인가?

결론은 그때그때 다르다. 빠르게 코드를 짤 수 있는 전략으로 시작해서 필요시 복잡한 전략으로 갈아탄다. 105p

가이드라인을 정하고 매핑을 사용할 근거를 제시하기

## 인커밍 포트와 아웃고잉 포트를 활용하자!

인커밍 포트와 아웃 고잉 포트는 서로 다른 계층이 어떻게 통신해야하는지를 정의하고 

계층 사이에 매핑을 수행할지 여부와 어떤 매핑 전략을 선택할지가 포함된다.






