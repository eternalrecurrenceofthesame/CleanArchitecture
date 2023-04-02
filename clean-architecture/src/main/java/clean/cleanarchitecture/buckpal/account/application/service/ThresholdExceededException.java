package clean.cleanarchitecture.buckpal.account.application.service;

import clean.cleanarchitecture.buckpal.account.domain.Money;

public class ThresholdExceededException extends RuntimeException{

    public ThresholdExceededException(Money threshold, Money actual){
        super(String.format("송금 한계 금액을 초과했습니다!", actual, threshold));
        // "Maximum threshold for transferring money exceeded: tried to transfer %s but threshold is %s!"
    }
}
