package pers.cocoadel.gateway.router;

import java.util.List;

public class RandomHttpEndpointRouter implements HttpEndpointRouter{
    @Override
    public String route(List<String> endpoints) {
        assert endpoints != null && endpoints.size() > 0;
        return endpoints.get((int)(Math.random() * endpoints.size()));
    }
}
