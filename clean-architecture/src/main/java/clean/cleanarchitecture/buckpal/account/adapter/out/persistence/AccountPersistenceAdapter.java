package clean.cleanarchitecture.buckpal.account.adapter.out.persistence;

import clean.cleanarchitecture.buckpal.account.application.port.out.LoadAccountPort;
import clean.cleanarchitecture.buckpal.account.application.port.out.UpdateAccountStatePort;
import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.Activity;
import clean.cleanarchitecture.buckpal.common.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

@RequiredArgsConstructor
@PersistenceAdapter
public class AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountStatePort {

    private final SpringDataAccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final AccountMapper accountMapper;

    @Override
    public Account loadAccount(AccountId accountId, LocalDateTime baselineDate) {

        AccountJpaEntity account = accountRepository.findById(accountId.getValue())
                .orElseThrow(EntityNotFoundException::new);

        List<ActivityJpaEntity> activities = activityRepository.findByOwnerSince(accountId.getValue(),
                baselineDate);

        Long withdrawalBalance = orZero(activityRepository.getWithdrawalBalanceUntil(accountId.getValue(),
                baselineDate));

        Long depositBalance = orZero(activityRepository.getDepositBalanceUntil(accountId.getValue(),
                baselineDate));

        // 매핑
        return accountMapper.mapToDomainEntity(
                account,
                activities,
                withdrawalBalance,
                depositBalance
        );
    }

    private Long orZero(Long value){
        return value == null ? 0L : value;
    }

    @Override
    public void updateActivities(Account account) {

        for(Activity activity : account.getActivityWindow().getActivities()){
            // 매핑
            if(activity.getId() == null){
                activityRepository.save(accountMapper.mapToJpaEntity(activity));
            }
        }
    }
}
