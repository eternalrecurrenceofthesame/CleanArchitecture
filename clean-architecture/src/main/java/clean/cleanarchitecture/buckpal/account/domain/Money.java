package clean.cleanarchitecture.buckpal.account.domain;

import lombok.*;

import java.math.BigInteger;

@Value // value 가 붙으면 private final 필드가 된다
public class Money {

    public static Money ZERO = Money.of(0L);

    @NonNull // 롬복에서 지원하는 널체크 애노테이션
    private final BigInteger amount;


    /**
     * 도메인 규칙
     */
    public boolean isPositiveOrZero(){
        return this.amount.compareTo(BigInteger.ZERO) >= 0;
    }

    public boolean isNegative(){
        return this.amount.compareTo(BigInteger.ZERO) < 0;
    }

    public boolean isPositive(){
        return this.amount.compareTo(BigInteger.ZERO) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Money money){
        return this.amount.compareTo(money.amount) >= 0;
    }

    public boolean isGreaterThan(Money money){
        return this.amount.compareTo(money.amount) >= 1;
    }


    /**
     * 생성 메서드
     *
     * 밸류 타입은 불변객체로 만들어서 안전하게 보관해야한다 즉 새로운 값을 줄 때
     * 새로 객체를 만들어서 줘야한다.
     */
    public static Money of(long value){
        return new Money(BigInteger.valueOf(value));
    }

    public static Money add(Money a, Money b){

        return new Money(a.amount.add(b.amount));
    }

    public Money minus(Money money){
        return new Money(this.amount.subtract(money.amount));
    }

    public Money plus(Money money){
        return new Money(this.amount.add(money.amount));
    }

    public static Money subtract(Money a, Money b){
        return new Money(a.amount.subtract(b.amount));
    }


    public Money negate(){
        return new Money(this.amount.negate());
    }

}
