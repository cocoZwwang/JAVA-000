package pers.cocoadel.learning.hmily.order.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig
{
    @Bean
    public Logger.Level loggerLevel()
    {
        return Logger.Level.FULL;
    }
}
