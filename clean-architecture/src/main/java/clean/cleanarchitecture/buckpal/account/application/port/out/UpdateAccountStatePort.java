package clean.cleanarchitecture.buckpal.account.application.port.out;

import clean.cleanarchitecture.buckpal.account.domain.Account;

/**
 * 데이터베이스 계좌 상태를 업데이트 하기 위한 아웃고잉 포트
 */
public interface UpdateAccountStatePort {

    void updateActivities(Account account);
}
