package pers.cocoadel.cmq.comm.request;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.core.message.Describe;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerRequestBody extends RequestBody {
    private Describe describe;

    public void check() {
        checkNotNull(describe);
        checkArgument(Strings.isNullOrEmpty(describe.getTopic()));
        checkArgument(Strings.isNullOrEmpty(describe.getName()));
    }
}
