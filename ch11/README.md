## 의식적으로 지름길 사용하기

지름길을 방지하기 위해서는 지름길 자체를 파악해야 한다!

```
* 품질이 떨어진 코드에서 작업할 때 더 낮은 품질의 코드를 추가하기 쉽다.

* 코딩 규칙을 많이 어긴 코드에서 작업할 때 또 다른 규칙을 어기기 쉽다.

* 지름길을 많이 사용한 코드에서 작업할 때 또 다른 지름길을 추가하기 쉽다.
```

* 유스케이스 간 모델 공유하기

유스케이스는 각각 다른 입출력 모델을 가져아 한다. 같은 모델을 공유하게 되면 입출력 모델이 변경될 경우 두 유스케이스 모두 영향을 받게 된다.

유스케이스간 모델을 공유해야하는 경우는 유스케이스들이 기능적으로 묶여 있을 때이다.

(특정 요구사항을 공유할 때 - 특정 세부사항을 변경해서 두 유스케이스에 모두 영향을 주고 싶은 경우)

유스케이스간 서로 미치는 영향 없이 독립적으로 진화해야 한다면 입출력 모델을 공유하는 방식은 **지름길**이 된다.

비슷한 개념의 유스케이스를 여러 개 만든다면 유스케이스를 독립적으로 진화할 필요가 있는지 주기적으로 질문해야 한다.


* 도메인 엔티티를 입출력 모델로 사용하기

인커밍 포트에서 입출력 모델로 도메인에 의존하게 되면 변경할 이유가 생기게 된다. 

도메인에 존재하지 않는 정보를 유스케이스에서 필요로 하면 유스케이스는 또 다른 도메인을 입출력 모델로 받아야 한다.

이런 경우 기존 도메인 모델과 다른 도메인 모델을 합치고 싶을 수 있음.

간단한 생성이나 업데이트 유스케이스에서 도메인 상태 그대로 저장하는 것이라면 도메인 값을 그대로 받을 수도 있겠지만

도메인 로직을 구현하고 도메인 로직의 일부를 풍부한 도메인 엔티티로 위임해야 한다면 유스케이스 인터페이스에 대한 전용

입출력 모델을 만들어야 한다.

왜냐하면 유스케이스의 변경이 도메인 엔티티까지 전파될 수 있기 떄문이다.

* 인커밍 포트 건너뛰기

포트 인터페이스 없이 인커밍 어댑터에서 모델 값으로 직접 서비스를 박스 타격 하면 진입점을 식별하기 어렵다.

인커밍 포트 인터페이스를 유지함으로써 특정 유스케이스를 구현하기 위해 어떤 서비스 메서드를 호출해야하는지 

쉽게 알 수 있다

또 아키텍처를 강제할 수 있다 아키텍처를 강제하는 옵션들을 이용하면 인커밍 어댑터가 포트를 호출하게 하고

인커밍 어댑터에서 호출할 의도가 없는 메서드를 실수로 호출하지 않게 된다.

* 애플리케이션 서비스 건너뛰기

간단한 CRUD 는 도메인 로직 없이 구현할 수 있다 (생성, 업데이트, 삭제 요청) 하지만 

서비스를 구현하지 않고 어댑터 포트 인터페이스를 통해 도메인에 직접 접근하는 유스케이스 처럼 만들면

인커밍 어댑터와 아웃고잉 어댑터가 서로 모델을 공유해야 한다.

유스케이스를 구현하고 유스케이스가 복잡해져서 간단한 CRUD 아웃고잉 어댑터에서 직접 도메인 모델로 처리하게 하면

도메인 로직이 흩어져서 도메인 로직을 찾거나 유지보수하는 것이 어려워 진다.


## 결론

지름길을 방지하기 위해 지름길을 잘 파악해야한다.

간단한 CRUD 상태에서 벗어나지 않는 유스케이스는 지름길을 이용하는 것이 합리적일 수도 있다.

이런 경우 지름길에서 벗어나는 시점이 언제인지에 대해 팀에서 합의하는 것이 매우 중요하다.

또 지름길을 선택한다면 왜 선택했는지에 대해 기록을 남겨 프로젝트를 인계받는 이들이 결정에 대해서

다시 평가할 수 있도록 하자.






