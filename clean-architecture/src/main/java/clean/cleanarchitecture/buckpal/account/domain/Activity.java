package clean.cleanarchitecture.buckpal.account.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import java.time.LocalDateTime;

/**
 * 계좌간 송금 활동을 만드는 클래스
 */

@Value
@RequiredArgsConstructor
@Getter
public class Activity {

    private ActivityId id;

    /**
     * 활동의 주체가 되는 계좌 아이디
     */
    @NonNull
    private final Account.AccountId ownerAccountId;

    /**
     * The debited account. 차변 계좌 아이디 (차변은 자산)
     */
    @NonNull
    private final Account.AccountId sourceAccountId;

    /**
     * The credited account. 대변 계좌 아이디 (대변은 부채와 자본)
     */
    @NonNull
    private final Account.AccountId targetAccountId;

    @NonNull
    private final LocalDateTime timestamp; // 활동 시간

    @NonNull
    private final Money money; // 계좌간 이동하는 금액

    public Activity(
            @NonNull Account.AccountId ownerAccountId,
            @NonNull Account.AccountId sourceAccountId,
            @NonNull Account.AccountId targetAccountId,
            @NonNull LocalDateTime timestamp,
            @NonNull Money money){
        this.id = null;
        this.ownerAccountId = ownerAccountId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.timestamp = timestamp;
        this.money = money;
    }


    @Value
    public static class ActivityId{
        private final Long value;
    }
}
