package clean.cleanarchitecture.buckpal.account.adapter.in.web;

import clean.cleanarchitecture.buckpal.account.application.port.in.SendMoneyCommand;
import clean.cleanarchitecture.buckpal.account.application.port.in.SendMoneyUseCase;
import clean.cleanarchitecture.buckpal.account.domain.Account;
import clean.cleanarchitecture.buckpal.account.domain.Money;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static clean.cleanarchitecture.buckpal.account.domain.Account.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SendMoneyController.class)
class SendMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendMoneyUseCase sendMoneyUseCase;

    @Test
    void testSendMoney() throws Exception {

        // Http 요청
        mockMvc.perform(post("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                41L,42L,500).header("Content-Type", "application/json"))
                .andExpect(status().isOk());

        // 컨트롤러에서 커멘드로 값 변경 후 유스케이스 호출
        then(sendMoneyUseCase).should()
                .sendMoney(eq(new SendMoneyCommand(
                        new AccountId(41L),
                        new AccountId(42L),
                        Money.of(500L))));

    }

}