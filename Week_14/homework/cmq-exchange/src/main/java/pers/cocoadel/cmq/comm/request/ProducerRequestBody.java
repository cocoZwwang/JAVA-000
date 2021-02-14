package pers.cocoadel.cmq.comm.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.core.message.Describe;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProducerRequestBody extends RequestBody{
    private Describe describe;

    public void check() {
//        checkArgument(!Strings.isNullOrEmpty(describe.getName()));
    }
}
