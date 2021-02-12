package pers.cocoadel.cmq.core.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Describe {
    private String topic;

    private String groupId;

    private String name;

    @Override
    public String toString() {
        return name + "@" + topic;
    }
}
