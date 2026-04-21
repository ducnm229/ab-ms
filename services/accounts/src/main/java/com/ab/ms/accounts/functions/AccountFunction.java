package com.ab.ms.accounts.functions;

import com.ab.ms.accounts.service.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountFunction {

    private static final Logger log = LoggerFactory.getLogger(AccountFunction.class);

    @Bean
    public Consumer<Long> updateCommunication(IAccountService accountService){
        return accountNumber -> {
            log.info("Updating communication status for account number: {}", accountNumber);
            accountService.updateCommunicationStt(accountNumber);
        };
    }
}
