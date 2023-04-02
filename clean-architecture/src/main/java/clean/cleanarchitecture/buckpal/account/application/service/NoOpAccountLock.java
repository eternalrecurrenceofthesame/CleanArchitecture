package clean.cleanarchitecture.buckpal.account.application.service;

import clean.cleanarchitecture.buckpal.account.application.port.out.AccountLock;
import clean.cleanarchitecture.buckpal.account.domain.Account;
import org.springframework.stereotype.Component;

/**
 * 테스트를 위한 락 구현 임시 객체
 */
@Component
public class NoOpAccountLock implements AccountLock {

    @Override
    public void lockAccount(Account.AccountId accountId) {
        // do nothing
    }

    @Override
    public void releaseAccount(Account.AccountId accountId) {
        // do nothing

    }
}
