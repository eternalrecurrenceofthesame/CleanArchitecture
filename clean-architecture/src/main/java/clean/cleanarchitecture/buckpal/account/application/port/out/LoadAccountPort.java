package clean.cleanarchitecture.buckpal.account.application.port.out;

import clean.cleanarchitecture.buckpal.account.domain.Account;

import java.time.LocalDateTime;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

/**
 * 계좌를 불러오는 아웃고잉 포트
 */
public interface LoadAccountPort {

    Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
}
