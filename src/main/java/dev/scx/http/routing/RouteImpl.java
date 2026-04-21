package dev.scx.http.routing;

import dev.scx.function.Function1Void;
import dev.scx.http.routing.method_matcher.MethodMatcher;
import dev.scx.http.routing.path_matcher.PathMatcher;
import dev.scx.http.routing.request_matcher.RequestMatcher;

/// RouteImpl 路由只保存状态不做行为处理
///
/// @author scx567888
/// @version 0.0.1
record RouteImpl(
    RequestMatcher requestMatcher,
    PathMatcher pathMatcher,
    MethodMatcher methodMatcher,
    Function1Void<RoutingContext, ?> handler
) implements Route {

    public RouteImpl {
        if (requestMatcher == null) {
            throw new NullPointerException("requestMatcher must not be null");
        }
        if (pathMatcher == null) {
            throw new NullPointerException("pathMatcher must not be null");
        }
        if (methodMatcher == null) {
            throw new NullPointerException("methodMatcher must not be null");
        }
        if (handler == null) {
            throw new NullPointerException("handler must not be null");
        }
    }

}
