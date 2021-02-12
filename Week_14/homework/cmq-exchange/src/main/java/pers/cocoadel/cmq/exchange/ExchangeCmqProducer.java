package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.core.message.Describe;

public abstract class ExchangeCmqProducer {

   public abstract void send(SendTextRequestBody requestBody);

   public abstract void removeProducer(Describe describe);
}
