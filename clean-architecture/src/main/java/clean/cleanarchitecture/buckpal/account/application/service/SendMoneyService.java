package clean.cleanarchitecture.buckpal.account.application.service;

import clean.cleanarchitecture.buckpal.account.application.port.in.SendMoneyCommand;
import clean.cleanarchitecture.buckpal.account.application.port.in.SendMoneyUseCase;
import clean.cleanarchitecture.buckpal.account.application.port.out.AccountLock;
import clean.cleanarchitecture.buckpal.account.application.port.out.LoadAccountPort;
import clean.cleanarchitecture.buckpal.account.application.port.out.UpdateAccountStatePort;
import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.common.UseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

@RequiredArgsConstructor
@UseCase // 유스 케이스를 표시하는 어노테이션 @Service 를 개조한 것
@Transactional
public class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final AccountLock accountLock;

    private final UpdateAccountStatePort updateAccountStatePort;
    private final MoneyTransferProperties moneyTransferProperties;

    @Override
    public boolean sendMoney(SendMoneyCommand command) {

        checkThreshold(command);

        // 10일 전
        LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);

        Account sourceAccount // 차변 계좌(차변은 자산)
                = loadAccountPort.loadAccount(command.getSourceAccountId(), baselineDate);

        Account targetAccount // 대변 계좌 아이디 (대변은 부채와 자본)
                = loadAccountPort.loadAccount(command.getTargetAccountId(), baselineDate);

        AccountId sourceAccountId
                = sourceAccount.getId().orElseThrow(() -> new IllegalArgumentException());
        AccountId targetAccountId
                = targetAccount.getId().orElseThrow(() -> new IllegalArgumentException());

        accountLock.lockAccount(sourceAccountId); // 추가
        if (!sourceAccount.withdraw(command.getMoney(), targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            return false;
        }

        accountLock.lockAccount(targetAccountId);
        if(!targetAccount.deposit(command.getMoney(), sourceAccountId)){
            accountLock.releaseAccount(sourceAccountId);
            accountLock.releaseAccount(targetAccountId);
            return false;
        }

        updateAccountStatePort.updateActivities(sourceAccount);
        updateAccountStatePort.updateActivities(targetAccount);

        accountLock.releaseAccount(sourceAccountId);
        accountLock.releaseAccount(targetAccountId);

        return true;
    }

    // Threshold 한계점
    private void checkThreshold(SendMoneyCommand command){
        if(command.getMoney().isGreaterThan(moneyTransferProperties.getMaximumTransferThreshold())){
            throw new ThresholdExceededException(moneyTransferProperties.getMaximumTransferThreshold(), command.getMoney());
        }
    }
}
