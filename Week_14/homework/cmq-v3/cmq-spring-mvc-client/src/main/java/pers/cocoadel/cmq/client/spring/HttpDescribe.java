package pers.cocoadel.cmq.client.spring;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.cocoadel.cmq.core.message.Describe;

@EqualsAndHashCode(callSuper = true)
@Data
public class HttpDescribe extends Describe {
    private String token;

    public HttpDescribe(String topic, String groupId, String token) {
        super(topic, groupId, token + "@" + topic);
        this.token = token;
    }

    public HttpDescribe(String token) {
        super(null, null, token);
    }

    public Describe getDescribe() {
        Describe describe = new Describe();
        describe.setName(getName());
        describe.setTopic(getTopic());
        describe.setGroupId(getGroupId());
        return describe;
    }
}
