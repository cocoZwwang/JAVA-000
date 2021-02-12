package pers.cocoadel.cmq.comm.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerResponseBody implements ResponseBody{

    private String topic;

    private String groupId;

    private String consumerName;
}
