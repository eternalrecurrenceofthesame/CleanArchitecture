package clean.cleanarchitecture.buckpal.account.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 계좌
 *
 * 계좌 클래스는 baseline 과 activityWindow 를 가진다.
 *
 * 비즈니스 요구사항에 따라서 기준 금액에 최근 활동 내역을 이용해서 예금을 더하거나 빼는
 * 구조로 계좌를 관리한다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    /**
     * 계좌 아이디
     */
    private AccountId id;

    /**
     * 최근 활동 내역을 추가하기 전 기준이되는 금액
     */
    private Money baselineBalance;

    /**
     * 최근 활동 내역, 이 내역을 토대로 기준 금액을 계산한다.
     * The window of latest activities on this account.
     */
    private ActivityWindow activityWindow;

    // 생성자와 Getter 생략

    public Optional<AccountId> getId(){
        return Optional.of(this.id);
    }

    // 생성 메서드들

    /**
     * 새 계좌를 만듦 아직 영속성 상태에 올라가기 전이라 아이디 값은 없다.
     */
    public static Account withoutId(Money baselineBalance,
                                    ActivityWindow activityWindow){
        return new Account(null, baselineBalance, activityWindow);
    }

    /**
     * 영속성 컨텍스트 값을 재구성 하기 위한 생성 메서드 얘는 왜 필요한지 아직몰름 ??
     */
    public static Account withId(AccountId accountId,
                                 Money baselineBalance,
                                 ActivityWindow activityWindow){
        return new Account(accountId, baselineBalance, activityWindow);
    }


    // 비즈니스 요구사항

    /**
     * 최근 활동 내역이랑 기준 금액을 더해서 계좌 금액을 정산하는 메서드
     */
    public Money calculateBalance(){
        return Money.add(this.baselineBalance,
                this.activityWindow.calculateBalance(this.id));
    }

    /**
     * 인출(출금) 메서드 인출 금액이 있는지 체크하고 로직을 수행한다
     */
    public boolean withdraw(Money money, AccountId targetAccountId){


        if(!mayWithdraw(money)){
            return false;
        }

        // 인출 활동 생성
        Activity withdrawal = new Activity(
                this.id,
                this.id,
                targetAccountId, // 대변 계좌 아이디 (빌려주는 계좌)
                LocalDateTime.now(),
                money);
        this.activityWindow.addActivity(withdrawal);
        return true;
    }

    // 비즈니스 규칙 검증 메서드
    private boolean mayWithdraw(Money money){
        return Money.add(
                this.calculateBalance(),
                money.negate())
                .isPositiveOrZero();
    }

    /**
     * 예금 메서드
     */
    public boolean deposit(Money money, AccountId sourceAccountId){

        // 예금 활동 생성
        Activity deposit = new Activity(
                this.id,
                sourceAccountId, // 차변 계좌 아이디 (차변은 자산)
                this.id,
                LocalDateTime.now(),
                money);

        this.activityWindow.addActivity(deposit);
        return true;
    }



    @Value
    public static class AccountId{
        private Long value;
    }


}
