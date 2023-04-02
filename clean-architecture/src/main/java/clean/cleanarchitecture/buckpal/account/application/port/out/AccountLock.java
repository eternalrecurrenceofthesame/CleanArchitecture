package clean.cleanarchitecture.buckpal.account.application.port.out;

import clean.cleanarchitecture.buckpal.account.domain.Account;

public interface AccountLock {

    void lockAccount(Account.AccountId accountId);
    void releaseAccount(Account.AccountId accountId);
}
