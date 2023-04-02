package clean.cleanarchitecture.buckpal.account.domain;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.*;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

/**
 * 거래 금액 내역을 보여주는 클래스
 */
public class ActivityWindow {

    /**
     * 화면에서 보여 줄 거래 활동들
     */
    private List<Activity> activities;

    /** 비즈니스 규칙 */

    // 첫 거래 시간을 보여주는 메서드
    public LocalDateTime getStartTimestamp(){
        return activities.stream()
                .min(Comparator.comparing(Activity::getTimestamp))
                .orElseThrow(IllegalStateException::new)
                .getTimestamp();
    }

    // 마지막 거래 시간을 보여주는 메서드
    public LocalDateTime getEndTimestamp(){
        return activities.stream()
                .max(Comparator.comparing(Activity::getTimestamp))
                .orElseThrow(IllegalStateException::new)
                .getTimestamp();
    }


    /**
     * 잔액 계산 메서드 calculateBalance
     *
     * 저장된 최근 활동 내역에서 예금 활동, 인출 활동을 찾고 차액을 계좌 금액에 더하면
     * 현재 잔액이된다.
     */
    public Money calculateBalance(AccountId accountId){
        Money depositBalance = activities.stream()
                .filter(a -> a.getTargetAccountId().equals(accountId)) // 대변 계좌(대변은 부채와 자본)
                .map(Activity::getMoney)
                .reduce(Money.ZERO, Money::add); // ??

        Money withdrawalBalance = activities.stream()
                .filter(a -> a.getSourceAccountId().equals(accountId)) // 차변 계좌(차변은 자산)
                .map(Activity::getMoney)
                .reduce(Money.ZERO, Money::add);

        return Money.add(depositBalance, withdrawalBalance.negate());
    }


    public ActivityWindow(@NonNull List<Activity> activities){
        this.activities = activities;
    }

    public ActivityWindow(@NonNull Activity... activities){
        this.activities = new ArrayList<>(Arrays.asList(activities));
    }

    public List<Activity> getActivities(){
        return Collections.unmodifiableList(this.activities);
    }

    public void addActivity(Activity activity){
        this.activities.add(activity);
    }

}
