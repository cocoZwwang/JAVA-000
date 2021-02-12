package pers.cocoadel.cmq.server.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.cocoadel.cmq.comm.request.*;
import pers.cocoadel.cmq.comm.response.AuthResponseBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.exchange.ExchangeCmqConsumer;
import pers.cocoadel.cmq.exchange.ExchangeCmqProducer;
import pers.cocoadel.cmq.exchange.ProducerKey;

import java.util.UUID;

@RestController
public class CmqController {

    @Autowired
    private ExchangeCmqConsumer<String> consumer;

    @Autowired
    private ExchangeCmqProducer producer;

    @PostMapping("/connect")
    public AuthResponseBody connect(@RequestBody AuthRequestBody requestBody) {
        AuthResponseBody body = new AuthResponseBody();
        body.setToken(UUID.randomUUID().toString().replace("-", ""));
        return body;
    }

    @PostMapping("/disconnect")
    public void unConnect(@RequestBody AuthRequestBody requestBody) {
//        producer.removeProducer(new ProducerKey(requestBody.getToken()));
//        consumer.removeConsumer(requestBody.getToken());
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody ConsumerRequestBody requestBody) {
        consumer.subscribe(requestBody);
    }

    @PostMapping("/poll")
    public PollResponseBody poll(@RequestBody PollRequestBody requestBody) {
        return consumer.poll(requestBody);
    }

    @PostMapping("/send")
    public void send(@RequestBody SendTextRequestBody requestBody) {
        producer.send(requestBody);
    }

    @PostMapping("/commit")
    public void commit(@RequestBody ConsumerRequestBody requestBody) {
        consumer.commit(requestBody);
    }
}
