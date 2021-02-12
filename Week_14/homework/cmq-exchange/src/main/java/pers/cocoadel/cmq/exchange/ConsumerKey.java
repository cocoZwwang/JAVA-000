package pers.cocoadel.cmq.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.cocoadel.cmq.core.message.Describe;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ConsumerKey {
    private final String token;

    private final Describe describe;

    public String toString() {
        return describe.toString() + "@" + token;
    }
}
