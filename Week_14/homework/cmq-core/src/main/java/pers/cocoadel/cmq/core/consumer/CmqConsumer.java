package pers.cocoadel.cmq.core.consumer;


import pers.cocoadel.cmq.core.message.CmqMessage;
import java.util.List;

public interface CmqConsumer<T> {

    boolean subscribe(String topic);

    CmqMessage<T> pollNow();

    List<CmqMessage<T>> pollNow(int count);

    boolean commit();
}
