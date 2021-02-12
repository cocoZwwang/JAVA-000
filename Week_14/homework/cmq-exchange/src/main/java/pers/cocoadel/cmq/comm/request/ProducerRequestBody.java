package pers.cocoadel.cmq.comm.request;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.core.message.Describe;

import static com.google.common.base.Preconditions.checkArgument;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProducerRequestBody extends RequestBody{
    private Describe describe;

    public void check() {
        checkArgument(Strings.isNullOrEmpty(describe.getName()));
    }
}
