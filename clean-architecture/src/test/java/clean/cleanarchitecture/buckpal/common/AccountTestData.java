package clean.cleanarchitecture.buckpal.common;


import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.ActivityWindow;
import clean.cleanarchitecture.buckpal.account.domain.Money;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

public class AccountTestData {

    public static AccountBuilder defaultAccount(){
        return new AccountBuilder()
                .withAccountId(new AccountId(42L))
                .withBaselineBalance(Money.of(999L))
                .withActivityWindow(new ActivityWindow(
                        ActivityTestData.defaultActivity().build(),
                        ActivityTestData.defaultActivity().build()));

    }
    public static class AccountBuilder{

        private AccountId accountId;
        private Money baselineBalance;
        private ActivityWindow activityWindow;

        public AccountBuilder withAccountId(AccountId accountId){
            this.accountId = accountId;
            return this;
        }

        public AccountBuilder withBaselineBalance(Money baselineBalance){
            this.baselineBalance = baselineBalance;
            return this;
        }

        public AccountBuilder withActivityWindow(ActivityWindow activityWindow){
            this.activityWindow = activityWindow;
            return this;
        }

        public Account build(){
            return withId(this.accountId, this.baselineBalance, this.activityWindow);
        }





    }

    
}
