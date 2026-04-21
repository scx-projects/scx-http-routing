package dev.scx.http.routing.request_matcher;

import dev.scx.http.ScxHttpServerRequest;

import static dev.scx.http.routing.request_matcher.AnyRequestMatcher.ANY_REQUEST_MATCHER;

/// RequestMatcher
///
/// @author scx567888
/// @version 0.0.1
public interface RequestMatcher {

    static RequestMatcher any() {
        return ANY_REQUEST_MATCHER;
    }

    static RequestMatcher typeIs(Class<? extends ScxHttpServerRequest> requestType) {
        return new TypeIsRequestMatcher(requestType);
    }

    static RequestMatcher typeNot(Class<? extends ScxHttpServerRequest> requestType) {
        return new TypeNotRequestMatcher(requestType);
    }

    boolean matches(ScxHttpServerRequest request);

}
