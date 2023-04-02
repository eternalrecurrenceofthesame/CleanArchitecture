package clean.cleanarchitecture.buckpal.account.adapter.out.persistence;

import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.ActivityWindow;
import clean.cleanarchitecture.buckpal.account.domain.Money;
import clean.cleanarchitecture.buckpal.common.AccountTestData;
import clean.cleanarchitecture.buckpal.common.ActivityTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;
import static clean.cleanarchitecture.buckpal.common.AccountTestData.*;
import static clean.cleanarchitecture.buckpal.common.ActivityTestData.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({AccountPersistenceAdapter.class, AccountMapper.class})
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter adapterUnderTest;

    @Autowired
    private ActivityRepository activityRepository; // 얘는 왜 따로?



    @Test
    @Sql("/AccountPersistenceAdapterTest.sql") // / 을 넣어야 한다
    void loadsAccount(){
        Account account = adapterUnderTest.loadAccount(
                new AccountId(1L), LocalDateTime.of(2018, 8, 10, 0, 0));

        assertThat(account.getActivityWindow().getActivities()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }

    @Test
    void updatesActivities() {

        Account account = defaultAccount().withBaselineBalance(Money.of(555L))
                .withActivityWindow(new ActivityWindow(
                        defaultActivity()
                                .withId(null)
                                .withMoney(Money.of(1L)).build()))
                .build();

        adapterUnderTest.updateActivities(account);

        assertThat(activityRepository.count()).isEqualTo(1);

        // 활동 내역 저장 테스트
        ActivityJpaEntity savedActivity = activityRepository.findAll().get(0);
        assertThat(savedActivity.getAmount()).isEqualTo(1L);
    }






}