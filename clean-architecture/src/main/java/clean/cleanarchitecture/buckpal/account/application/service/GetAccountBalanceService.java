package clean.cleanarchitecture.buckpal.account.application.service;

import clean.cleanarchitecture.buckpal.account.application.port.in.GetAccountBalanceQuery;
import clean.cleanarchitecture.buckpal.account.application.port.out.LoadAccountPort;
import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.Money;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

/**
 * 계좌 잔액 조회
 */

@RequiredArgsConstructor
public class GetAccountBalanceService implements GetAccountBalanceQuery {

    private final LoadAccountPort loadAccountPort;

    @Override
    public Money getAccountBalance(AccountId accountId) {
        return loadAccountPort.loadAccount(accountId, LocalDateTime.now())
                .calculateBalance();
    }
}
