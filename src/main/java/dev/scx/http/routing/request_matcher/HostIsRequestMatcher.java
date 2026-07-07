package dev.scx.http.routing.request_matcher;

import dev.scx.http.ScxHttpServerRequest;

import static dev.scx.http.headers.HttpHeaderName.HOST;

/// HostIsRequestMatcher
///
/// 精确匹配 (忽略大小写)
///
/// @author scx567888
public record HostIsRequestMatcher(String host) implements RequestMatcher {

    public HostIsRequestMatcher {
        if (host == null) {
            throw new NullPointerException("host must not be null");
        }
    }

    @Override
    public boolean matches(ScxHttpServerRequest request) {
        var requestHost = request.getHeader(HOST);
        return this.host.equalsIgnoreCase(requestHost);
    }

    @Override
    public String toString() {
        return "hostIs(" + host + ")";
    }

}
