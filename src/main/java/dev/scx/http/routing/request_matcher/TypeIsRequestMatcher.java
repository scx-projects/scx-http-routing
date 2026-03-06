package dev.scx.http.routing.request_matcher;

import dev.scx.http.ScxHttpServerRequest;

/// TypeIsRequestMatcher
///
/// @author scx567888
/// @version 0.0.1
public record TypeIsRequestMatcher(Class<? extends ScxHttpServerRequest> requestType) implements RequestMatcher {

    public TypeIsRequestMatcher {
        if (requestType == null) {
            throw new NullPointerException("requestType must not be null");
        }
    }

    @Override
    public boolean matches(ScxHttpServerRequest request) {
        return requestType.isInstance(request);
    }

    @Override
    public String toString() {
        return "typeIs(" + requestType.getSimpleName() + ")";
    }

}
