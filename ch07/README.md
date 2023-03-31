## 아키텍처 요소 테스트하기

비용이 적고, 유지보수하기 쉽고, 빨리 실행되고, 안정적인 작은 크기의 테스트를 만들자!

테스트 피라미드에 따르면 비용이 많이 드는 테스트는 지양하고 비용이 적게 드는 테스트를 많이 만들어야 한다.
```
시스템 테스트

통합 테스트

단위 테스트
```

* 단위 테스트

단위 테스트는 테스트 피라미드의 토대가 된다. 일반적으로 하나의 클래스를 인스턴스화 하고 

해당 클래스의 인터페이스를 통해 기능들을 테스트한다. 테스트하는 클래스가 다른 클래스에 의존하고 있다면

의존되는 클래스는 인스턴스화 하지 않고 테스트하는 동안 목(mock) 클래스로 대체한다.

* 통합 테스트

시작점이 되는 클래스의 인터페이스로 데이터를 보낸 후 유닛들의 네트워크가 기대한 대로 잘 동작하는지 검증

* 시스템 테스트

애플리케이션을 구성하는 모든 객체 네트워크를 가동시켜 특정 유스케이스가 전 계층에서 잘 동작하는지 검증

## 단위 테스트로 도메인 엔티티 테스트하기

* AccountTest

Account 모델을 테스트하기 위해 테스트용 계좌와 계좌 활동 빌더를 만들었다. (AccountTestData, ActivitTestData)

```
@Test
    void withdrawalSucceeds(){
        AccountId accountId = new AccountId(1L); // 생성자 호출

        Account account = defaultAccount()
                .withAccountId(accountId)
                .withBaselineBalance(Money.of(555L))
                .withActivityWindow(new ActivityWindow(
                        defaultActivity()
                                .withTargetAccount(accountId) // 빌려주는 계좌
                                .withMoney(Money.of(999L)).build(),
                        defaultActivity()
                                .withTargetAccount(accountId)
                                .withMoney(Money.of(1L)).build()))
                .build();

        Money balance = account.calculateBalance(); // 계좌 내역으로 기준 잔액 맞추기

        assertThat(balance).isEqualTo(Money.of(1555L)); // 기본 금액 + 차변 금액을 더함 (차변은 자산)
        assertThat(account.getActivityWindow().getActivities()).hasSize(2); // 활동 내역 개수 2 개 
        assertThat(account.calculateBalance()).isEqualTo(Money.of(1555L)); 
        
        boolean success = account.withdraw(Money.of(555L), new AccountId(99L)); // withdraw 가능한 자산이 있는지 테스트.
        assertThat(success).isTrue();

 }
```    

간단한 테스트 예시.

Account 의 필드값에 Actvity(Mock 의 역할) 를 만들어서 넣고 Account 의 기본 기능들을 한번씩 사용해봄. 테스트 패키지 참고.

## 단위 테스트로 유스케이스 테스트하기

인커밍 포트 인터페이스가 구현되는 유스케이스를 테스트하기

SendMoneyService 유스케이스는 커멘드 모델의 금액 한도를 체크하고 (100 만원), 차변 계좌와 대변 계좌를 로드해온다.

차변 계좌와 대변 계좌의 유무를 체크하고, 대변 계좌에 락을 걸고 차변계좌에서 대변 계좌로 한도 금액 만큼 전송한다 

그리고 각각의 계좌 활동 내역을 업데이트한 후 락을 해제.

```
Tip 

차변은 자산, 대변은 부채와 자본
```

SendMoneyServiceTest 참고.

테스트한 유스케이스 서비스는 상태가 없기 때문에 then 섹션에서 특정 상태를 검증할 수 없다??

대신 테스트는 서비스가 (모킹된) 의존 대상의 특정 메서드와 **상호작용**이 되었는지 여부를 검증한다. 상호 작용을 검증하는 것은

테스트 코드의 행동 변경뿐만 아니라 코드의 구조 변경에도 취약해진다는 의미가 된다.?? 의존하고 있는 클래스가 리팩토링 되면 테스트도 변경될 확률이 높다.

상호작용을 테스트 할 때는 모든 동작을 검증하는 대신 중요한 핵심만 골라서 테스트 하는 것이 좋다.

이 테스트는 유스케이스와 연결된 포트와 락에 의존한 상호작용을 테스트하고 있는 통합 테스트에 가깝지만 Mock 객체로 작업하고 

있기 때문에 실제 의존성을 관리하지 않아도 된다 (완전한 통합 테스트에 비해 유지보수가 유리)

#### + Mock 을 이용한 테스트 예시들 

간단하게 Mock 을 이용한 코드들을 정리해보겠다.

```
* private final LoadAccountPort loadAccountPort = Mockito.mock(LoadAccountPort.class);

계좌를 가져오는 아웃 고잉 포트를 목 객체로 만듦(ORM 에 해당함)

* private Account givenAnAccountWithId(AccountId id){
        Account account = Mockito.mock(Account.class);

        given(account.getId()).willReturn(Optional.of(id));
        given(loadAccountPort.loadAccount(eq(account.getId().get()), any(LocalDateTime.class)))
                .willReturn(account);

        return account;
    }
    
Mock 계좌를 만들고 Mock 아웃 고잉 포트 만든 계좌를 가지고 올 수 있는지 테스트함

eq 를 이용하면 정확히 일치하는 값을 가지고 옴, any 는 해당 타입이면 됨 willReturn 으로 리턴 되는지 체크

https://www.digitalocean.com/community/tutorials/mockito-argument-matchers-any-eq 참고


* then(sourceAccount).should().withdraw(eq(money), eq(targetAccountId));

then 은 given / when / then 의 then , withdraw (출금 메서드) 상호 작용이 일어나는지 테스트한다
```

## 통합 테스트로 웹 어댑터 테스트하기

SendMoneyControllerTest 참고

```
 // Http 요청
        mockMvc.perform(post("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                41L,42L,500).header("Content-Type", "application/json"))
                .andExpect(status().isOk());

        // 컨트롤러에서 커멘드로 값 변경 후 유스케이스 호출
        then(sendMoneyUseCase).should()
                .sendMoney(eq(new SendMoneyCommand(
                        new AccountId(41L),
                        new AccountId(42L),
                        Money.of(500L))));
```

단순히 컨트롤러를 테스트한 단위테스트 처럼 보이지만 통합 테스트이다. @WebMvcTest 애노테이션을 사용하면

스프링이 경로 요청, 자바와 JSON 간의 매핑, HTTP 입력 검증 등에 필요한 전체 객체 네트워크를 인스턴스화 하도록 만든다.

그리고 테스트에서 웹 컨트롤러가 아닌 이 네트워크의 일부로서 잘 동작하는지 검증한다

웹 컨트롤러가 스프링 프레임워크에 강하게 묶여있기 때문에 격리된 상태로 단위테스트를 하는 것 보다 프레임워크와

통합된 상태로 테스트하는 것이 합리적이다! 

단순히 단위테스트를 하게 되면 매핑, 유효성 검증, HTTP 항목에 대한 커버리지가 낮아지고 프레임 워크를 구성하는 요소들이

프로덕션 환경?? 에서 정상적으로 작동할지 확신할 수 없게된다.


## 통합 테스트로 영속성 어댑터 테스트하기

영속성 어댑터를 테스트 할 때도 통합 테스트를 진행해서 JPA 를 통해 데이터베이스에 매핑이 되는지 검증해야 한다.

AccountPersistenceAdapterTest 참고

```
@DataJpaTest - 스프링 데이터 리포지터리를 포함한 데이터베이스 접근에 필요한 객체 네트워크 인스턴스화 선언

@Import - @DataJpaTest 외에 추가로 필요한 객체들을 가지고 온다.

@Sql - 테스트시 사용할 Sql 데이터를 인잇 할 때 사용 resources 에 경로 추가! 
ex) @Sql("/AccountPersistenceAdapterTest.sql") // / 을 넣어야 한다!! /
```

AccountPersistenceAdapterTest 에서는 데이터베이스를 모킹하지 않았다는 점이 중요하다. (테스트가 실제로 데이터베이스에 접근)

모킹하지 않고 실제 데이터베이스에 접근함으로써 모킹했을 때 발견하지 못하는 데이터베이스 문제점까지 커버리지 할 수 있다.

Tip

H2 데이터베이스를 사용하면 테스트시 메모리에 H2 임베디드 데이터베이스를 올려서 사용가능함!

인메모리 사용시 실제 사용할 데이터베이스와 Sql 문법상 다를 수 있기 때문에 주의해야한다.

정석대로 테스트하면 영속성 어댑터 테스트는 실제 데이터베이스를 대상으로 진행해야한다 

Testcontainers 같은 라이브러리가 있음! www.testcontainers.org 참고


## 시스템 테스트로 주요 경로 테스트하기

전체 애플리케이션을 띄우고 Account API 요청이 잘 동작하는지 테스트해보기

buckpal-sendMoneySystemTest

```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
스프링이 애플리케이션을 구성하는 모든 객체 네트워크를 띄움 + 랜덤 포트 설정 
 
@Autowired
private TestRestTemplate restTemplate; 을 이용해서 실제 HTTP 통신

@Autowired
private LoadAccountPort loadAccountPort; 
실제 HTTP 통신 하는 것 처럼 출력 어댑터 사용, 데이터베이스를 연결하는 영속성 어댑터

다른 출력 어댑터를 사용할 수도 있음 시스템 테스트라고 하더라도 언제나 서드파티 시스템을 

실행해서 테스트할 수 있는 것이 아니기 때문에 모킹해서 어댑터를 받아와야 할 수도 있다
```

```
그외 테스트 가독성을 높이기 위해 지저분한 로직들을 헬퍼 메서드로 만들어서 테스트 가독성을 높였다

헬퍼메서드가 여러가지 상태를 검증할 때 사용할 수 있는 도메인 특화 언어를 제공한다

도메인 특화 언어는 어떤 테스트에서 사용해도 유용하지만 시스템 테스트에서 더욱 의미를 가진다.
```

## 얼마 만큼의 테스트가 충분할까?




