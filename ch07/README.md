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







