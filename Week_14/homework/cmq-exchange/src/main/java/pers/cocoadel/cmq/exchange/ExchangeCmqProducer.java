package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.request.SendTextRequestBody;

public abstract class ExchangeCmqProducer {

   public abstract void send(SendTextRequestBody requestBody);

   public abstract void removeProducer(ProducerKey key);
}
