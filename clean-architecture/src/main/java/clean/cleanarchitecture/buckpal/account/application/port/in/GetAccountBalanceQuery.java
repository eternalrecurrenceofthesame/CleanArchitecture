package clean.cleanarchitecture.buckpal.account.application.port.in;

import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.Account.AccountId;
import clean.cleanarchitecture.buckpal.account.domain.Money;

public interface GetAccountBalanceQuery {

    Money getAccountBalance(AccountId accountId);
}
