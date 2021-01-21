package pers.cocoadel.cmq.server.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.spi.ObjectFactory;
import pers.cocoadel.cmq.exchange.ExchangeCmqConsumer;
import pers.cocoadel.cmq.exchange.ExchangeCmqProducer;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqConsumer;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqProducer;

@SpringBootApplication
public class CmqApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmqApplication.class,args);
    }

    @Bean
    public ExchangeCmqProducer exchangeCmqProducer(CmqBroker cmqBroker) {

        ServerExchangeCmqProducer exchangeCmqProducer = new ServerExchangeCmqProducer();
        exchangeCmqProducer.setBroker(cmqBroker);
        return exchangeCmqProducer;
    }

    @Bean
    public ExchangeCmqConsumer<String> exchangeCmqConsumer(CmqBroker cmqBroker) {
        ServerExchangeCmqConsumer serverExchangeCmqConsumer = new ServerExchangeCmqConsumer();
        serverExchangeCmqConsumer.setCmqBroker(cmqBroker);
        return serverExchangeCmqConsumer;
    }

    @Bean
    public CmqBroker localCmqBroker(){
        return ObjectFactory.createObject(CmqBroker.class);
    }
}
