package clean.cleanarchitecture.buckpal.account.application.port.in;

import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.Money;
import clean.cleanarchitecture.buckpal.common.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.NotNull;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

@Value
@EqualsAndHashCode(callSuper = false)
public class SendMoneyCommand extends SelfValidating<SendMoneyCommand> {

    @NotNull
    private final AccountId sourceAccountId;

    @NotNull
    private final AccountId targetAccountId;

    @NotNull
    private final Money money;

    public SendMoneyCommand(AccountId sourceAccountId,
                            AccountId targetAccountId,
                            Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
        this.validateSelf(); // 이거 넣어줘야 하는듯
    }
}
