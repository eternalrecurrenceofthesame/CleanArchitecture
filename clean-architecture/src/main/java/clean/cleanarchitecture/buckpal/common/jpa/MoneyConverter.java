package clean.cleanarchitecture.buckpal.common.jpa;

import clean.cleanarchitecture.buckpal.account.domain.Money;

import javax.persistence.AttributeConverter;
import java.math.BigInteger;

/**
 * AttributeConverter 2 개의 프로퍼티를 한 개의 칼럼에 매핑할 때 사용한다.
 */
public class MoneyConverter implements AttributeConverter<Money, BigInteger> {

    @Override
    public BigInteger convertToDatabaseColumn(Money money) {
        return money == null ? null : money.getAmount();
    }

    @Override
    public Money convertToEntityAttribute(BigInteger value) {
        return value == null ? null : new Money(value);
    }
}
