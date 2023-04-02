package clean.cleanarchitecture.buckpal.account.application.port.in;

/**
 * 인커밍 포트
 */
public interface SendMoneyUseCase {

    boolean sendMoney(SendMoneyCommand command);
}
