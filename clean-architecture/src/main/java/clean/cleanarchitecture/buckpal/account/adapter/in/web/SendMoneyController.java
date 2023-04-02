package clean.cleanarchitecture.buckpal.account.adapter.in.web;

import clean.cleanarchitecture.buckpal.account.application.port.in.SendMoneyCommand;
import clean.cleanarchitecture.buckpal.account.application.port.in.SendMoneyUseCase;
import clean.cleanarchitecture.buckpal.account.domain.Money;
import clean.cleanarchitecture.buckpal.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class SendMoneyController {

    private final SendMoneyUseCase sendMoneyUseCase; // 인터페이스에 의존 DIP

    @PostMapping(path = "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    void sendMoney(@PathVariable("sourceAccountId") Long sourceAccountId,
                   @PathVariable("targetAccountId") Long targetAccountId,
                   @PathVariable("amount") Long amount){

        // 매핑
        SendMoneyCommand command = new SendMoneyCommand(
                new AccountId(sourceAccountId),
                new AccountId(targetAccountId),
                Money.of(amount));

        sendMoneyUseCase.sendMoney(command);
    }
}
