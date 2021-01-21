package pers.cocoadel.cmq.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ConsumerKey {
    private final String token;

    private final String topic;

    private final String groupId;

    public String toString() {
        return token + ":" + topic + ":" + groupId;
    }
}
