package pers.cocoadel.cmq.core.broker;

import pers.cocoadel.cmq.core.spi.CmqConsumerFactory;
import pers.cocoadel.cmq.core.spi.CmqFactory;
import pers.cocoadel.cmq.core.spi.CmqProducerFactory;

public abstract class AbstractCmqBroker implements CmqBroker{
    protected boolean isAutoCreateTopic = true;

    protected CmqFactory cmqFactory;

    protected CmqConsumerFactory cmqConsumerFactory;

    protected CmqProducerFactory cmqProducerFactory;

    public void setCmqFactory(CmqFactory cmqFactory) {
        this.cmqFactory = cmqFactory;
    }

    public void setCmqConsumerFactory(CmqConsumerFactory cmqConsumerFactory) {
        this.cmqConsumerFactory = cmqConsumerFactory;
    }

    public void setCmqProducerFactory(CmqProducerFactory cmqProducerFactory) {
        this.cmqProducerFactory = cmqProducerFactory;
    }

    public boolean isAutoCreateTopic() {
        return isAutoCreateTopic;
    }

    public void setAutoCreateTopic(boolean autoCreateTopic) {
        isAutoCreateTopic = autoCreateTopic;
    }
}
