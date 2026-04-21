package com.ab.ms.message.function;

import com.ab.ms.message.dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunction {
    private static final Logger log = LoggerFactory.getLogger(MessageFunction.class);

    @Bean
    public Function<AccountsMsgDto, AccountsMsgDto> sendEmail() {
        return accountsMsgDto -> {
            log.info("Sending email with details: {}", accountsMsgDto.toString());
            return accountsMsgDto;
        };
    }

    @Bean
    public Function<AccountsMsgDto, Long> sendSms() {
        return accountsMsgDto ->  {
            log.info("Sending sms with details: {}", accountsMsgDto.toString());
            return accountsMsgDto.accountNumber();
        };
    }
}
