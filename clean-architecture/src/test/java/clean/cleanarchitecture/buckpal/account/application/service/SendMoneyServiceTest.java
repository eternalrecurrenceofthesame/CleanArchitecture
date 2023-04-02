package clean.cleanarchitecture.buckpal.account.application.service;

import clean.cleanarchitecture.buckpal.account.application.port.in.SendMoneyCommand;
import clean.cleanarchitecture.buckpal.account.application.port.out.AccountLock;
import clean.cleanarchitecture.buckpal.account.application.port.out.LoadAccountPort;
import clean.cleanarchitecture.buckpal.account.application.port.out.UpdateAccountStatePort;
import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.Money;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class SendMoneyServiceTest {

    private final LoadAccountPort loadAccountPort
            = Mockito.mock(LoadAccountPort.class);

    private final AccountLock accountLock
            = Mockito.mock(AccountLock.class);

    private final UpdateAccountStatePort updateAccountStatePort
            = Mockito.mock(UpdateAccountStatePort.class);

    private final SendMoneyService sendMoneyService =
            new SendMoneyService(loadAccountPort, accountLock, updateAccountStatePort, moneyTransferProperties());

    // 금액 전송 제한 없음.
    private MoneyTransferProperties moneyTransferProperties(){
        return new MoneyTransferProperties(Money.of(Long.MAX_VALUE));
    }

    @Test
    void transactionSucceeds(){

        Account sourceAccount = givenSourceAccount();
        Account targetAccount = givenTargetAccount();

        // given 출금 입금 테스트
        givenWithdrawalWillSucceed(sourceAccount);
        givenDepositWillSucceed(targetAccount);

        Money money = Money.of(500L);

        // 유스케이스 커멘드 생성
        SendMoneyCommand command = new SendMoneyCommand(
                sourceAccount.getId().get(),
                targetAccount.getId().get(),
                money);

        boolean success = sendMoneyService.sendMoney(command);

        /**
         * 전체 송금 유스케이스 테스트
         */
        assertThat(success).isTrue();

        /**
         * 송금 유스케이스 부분 테스트
         */
        AccountId sourceAccountId = sourceAccount.getId().get(); // 41
        AccountId targetAccountId = targetAccount.getId().get(); // 42



        then(accountLock).should().lockAccount(eq(sourceAccountId));
        // 차변 계좌가 대변 계좌로 인출 했는지 체크
        then(sourceAccount).should().withdraw(eq(money), eq(targetAccountId));
        then(accountLock).should().releaseAccount(eq(sourceAccountId));


        then(accountLock).should().lockAccount(eq(targetAccountId));
        // 대변 계좌 자산이 증가했는지 체크
        then(targetAccount).should().deposit(eq(money), eq(sourceAccountId));
        then(accountLock).should().releaseAccount(eq(targetAccountId));


        // 활동 내역 업데이트 확인
        thenAccountsHaveBeenUpdated(sourceAccountId, targetAccountId);

    }

    private void thenAccountsHaveBeenUpdated(AccountId ... accountIds){

        // Captor 획득자
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        then(updateAccountStatePort).should(times(accountIds.length))
                .updateActivities(accountCaptor.capture());

        List<AccountId> updatedAccountIds = accountCaptor.getAllValues()
                .stream()
                .map(Account::getId)
                .map(Optional::get)
                .collect(Collectors.toList());

        for(AccountId accountId : accountIds){
            assertThat(updatedAccountIds).contains(accountId);
        }
    }




    private void givenDepositWillSucceed(Account account){
        given(account.deposit(any(Money.class), any(AccountId.class)))
                .willReturn(true);
    }

    private void givenWithdrawalWillFail(Account account){
        given(account.withdraw(any(Money.class), any(AccountId.class)))
                .willReturn(false);
    }

    private void givenWithdrawalWillSucceed(Account account){
        given(account.withdraw(any(Money.class), any(AccountId.class)))
                .willReturn(true);
    }

    // 대변 계좌 찾기 (대변은 부채와 자본)
    private Account givenTargetAccount(){
        return givenAnAccountWithId(new AccountId(42L));
    }


    // 차변 계좌 찾기 (차변은 자산)
    private Account givenSourceAccount(){
        return givenAnAccountWithId(new AccountId(41L));
    }

    private Account givenAnAccountWithId(AccountId id){
        Account account = Mockito.mock(Account.class);

        given(account.getId()).willReturn(Optional.of(id));
        given(loadAccountPort.loadAccount(eq(account.getId().get()), any(LocalDateTime.class)))
                .willReturn(account);

        return account;
    }
}