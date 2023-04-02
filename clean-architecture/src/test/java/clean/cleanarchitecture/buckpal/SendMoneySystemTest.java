package clean.cleanarchitecture.buckpal;

import clean.cleanarchitecture.buckpal.account.application.port.out.AccountLock;
import clean.cleanarchitecture.buckpal.account.application.port.out.LoadAccountPort;
import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.Money;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;
import static org.assertj.core.api.BDDAssertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendMoneySystemTest {

    /**
     * 테스트에 필요한 작은 것 부터 만들자 계좌 아이디, 기본 계좌 금액 등등
     */

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LoadAccountPort loadAccountPort;



    @Test
    @Sql("/SendMoneySystemTest.sql")
    void sendMoney(){

        Money initialSourceBalance = sourceAccount().calculateBalance();
        Money initialTargetBalance = targetAccount().calculateBalance();

        ResponseEntity response = whenSendMoney(
                sourceAccountId(),
                targetAccountId(),
                transferredAmount());


        //BDDAssertions
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        then(sourceAccount().calculateBalance())
                .isEqualTo(initialSourceBalance.minus(transferredAmount()));

        then(targetAccount().calculateBalance())
                .isEqualTo(initialTargetBalance.plus(transferredAmount()));

    }

    /**
     * 테스트 가독성을 높이기 위한 헬퍼 메서드들
     */

    private Account sourceAccount() {
        return loadAccount(sourceAccountId());
    }

    private Account targetAccount(){
        return loadAccount(targetAccountId());
    }

    private Account loadAccount(AccountId accountId){
        return loadAccountPort.loadAccount(
                accountId,
                LocalDateTime.now());
    }

    private ResponseEntity whenSendMoney(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Void> request = new HttpEntity<>(null, headers); // void ??

        return restTemplate.exchange(
                "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                HttpMethod.POST,
                request,
                Object.class,
                sourceAccountId.getValue(),
                targetAccountId.getValue(),
                amount.getAmount());
    }

    private Money transferredAmount(){
        return Money.of(500L);
    }

    // 계좌 찾고 활동 내역 가져와서 기준 금액 맞추는 메서드
    private Money balanceOf(AccountId accountId){
        Account account = loadAccountPort.loadAccount(accountId, LocalDateTime.now());
        return account.calculateBalance();
    }

    private AccountId sourceAccountId(){
        return new AccountId(1L);
    }
    private AccountId targetAccountId(){
        return new AccountId(2L);
    }
}
