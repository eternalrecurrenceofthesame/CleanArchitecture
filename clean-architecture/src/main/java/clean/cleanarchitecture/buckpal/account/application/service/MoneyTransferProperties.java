package clean.cleanarchitecture.buckpal.account.application.service;

import clean.cleanarchitecture.buckpal.account.domain.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component // 추가
public class MoneyTransferProperties {

    // Threshold 한계점
    private Money maximumTransferThreshold = Money.of(1_000_000L);
}
