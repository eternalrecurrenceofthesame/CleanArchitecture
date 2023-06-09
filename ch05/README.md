## 웹 어댑터 구현하기 
```
DIP 의존성 역전 - 코드상의 어떤 의존성이든 그 방향을 바꿀 수 (역전시킬 수) 있다.
OCP 개방 폐쇄의 원칙 - 어플리케이션은 확장에는 열려있지만 변경에는 닫혀 있어야 한다.
```

웹 어댑터는 외부로부터 호출받고 어플리케이션 코어를 호출하면서 무슨 일(command)을 해야하는지 알려준다.

이때 응용 서비스는 어댑터가 통신할 수 있는 특정 포트를 제공한다 SendMoneyUseCase sendMoneyUseCase

어댑터에서 직접 응용서비스를 타격하지 않고 포트를 제공받는 이유는 포트 인터페이스를 제공받음으로써 

DIP , OCP 를 지키며 스프링 DI  와 다형성을 이용해서 구현체를 사용할 수 있기 때문이다. 

또한 포트를 적절한 곳에 위치시킴으로써 외부와 어떤 통신이 일어나는지 정확히 알 수 있다.

웹 소켓을 이용해서 실시간 데이터를 전송하면 반드시 포트가 사용되어야 한다. 

## 웹 어댑터의 책임 

```
HTTP 요청을 자바 객체로 매핑

권한 검사

입력 유효성 검증 

입력을 유스케이스의 입력 모델로 매핑

유스케이스 호출

유스케이스의 출력을 HTTP 로 매핑

HTTP 응답을 반환

```

어댑터의 입력 유효성 검증은 유스케이스 입력 모델에서 하는 유효성 검증과는 다른 책임을 가진다. (둘이 구분됨)

**웹 어댑터의 입력 모델을 유스케이스의 입력 모델로 변환할 수 있다는 것을 검증해야 한다**

웹 어댑터와 응용 서비스 계층 간의 경계는 도메인과 응용 서비스 계층부터 개발하기 시작하면 자연스럽게 생긴다.

## 컨트롤러 나누기

하나의 컨트롤러에 모든 API 요청을 넣으면 안 된다. 계좌 유스케이스를 만들 때 하나의 AccountController 를 만들어서

계좌와 관련된 모든 요청을 받는 것은 바람직하지 않다. 각 컨트롤러는 가능한 한 적게 공유하는 웹 어댑터 조각으로 구현해야 한다. 

계좌 유스케이스 중 돈을 송금하는 유스케이스 컨트롤러를  SendMoneyController 처럼 좁은 범위로 만들어서 응집성을 높여야

테스트하거나 유지보수가 편해진다.

각 컨트롤러는 컨트롤러 자체의 모델을 가질 수 있다, 모델을 private 으로 만들고 같은 패키지 내에서만 HTTP 값을 받을 때 사용할 수 도 있고, 

SendMoneyController 처럼 원시 값을 받을 수도 있다.

컨트롤러를 만들 때는 메서드와 클래스명을 유스케이스를 최대한 반영해서 만들고 별도의 패키지별로 나누는 것이 좋다.


#### + 유지 보수 가능한 소프트웨어를 만들기 위해

어댑터는 HTTP 요청 값을 호출할 유스케이스의 커맨드 모델로 변환해서 주입받은 인커밍 포트로 커멘드 모델을 넘겨준다. 

어플리케이션 코어에서 명령을 수행하고 아웃 고잉 포트를 통해서 아웃 고잉 어댑터를 호출하면서 필요한 값을 넘겨준다.

아웃 고잉 어댑터에서는 요청 작업을 수행한 후 출력 값을 반환한다.

이 과정에서 어댑터는 어떠한 도메인 로직도 수행하지 않는 어댑터로 만들어야 한다. 

응용 서비스 계층에서는 HTTP 에 대한 상세 정보를 노출시키지 않도록 HTTP 와 관련된 작업을 해서는 안 된다. 

HTTP 정보에 의존하지 않음으로써 필요시 웹 어댑터를 다른 어댑터로 쉽게 교체할 수 있다.

컨트롤러를 구현할 때는 앞서 언급한 것 처럼 웹 어댑터 조각으로 구현해서 어댑터의 응집성을 높여야 한다. 



