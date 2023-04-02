package clean.cleanarchitecture.buckpal.account.domain;

import clean.cleanarchitecture.buckpal.common.AccountTestData;
import clean.cleanarchitecture.buckpal.common.ActivityTestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;
import static clean.cleanarchitecture.buckpal.common.AccountTestData.*;
import static clean.cleanarchitecture.buckpal.common.ActivityTestData.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void withdrawalSucceeds(){
        AccountId accountId = new AccountId(1L);

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

        Money balance = account.calculateBalance();


        assertThat(balance).isEqualTo(Money.of(1555L));
        assertThat(account.getActivityWindow().getActivities()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(1555L));


        boolean success = account.withdraw(Money.of(555L), new AccountId(99L));
        assertThat(success).isTrue();


    }

}