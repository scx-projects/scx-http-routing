package dev.scx.http.routing;

import dev.scx.function.Function1Void;
import dev.scx.http.routing.method_matcher.MethodMatcher;
import dev.scx.http.routing.path_matcher.PathMatcher;
import dev.scx.http.routing.request_matcher.RequestMatcher;

/// Route
///
/// @author scx567888
/// @version 0.0.1
public interface Route {

    static Route of(
        RequestMatcher requestMatcher,
        PathMatcher pathMatcher,
        MethodMatcher methodMatcher,
        Function1Void<RoutingContext, ?> handler) {
        return new RouteImpl(requestMatcher, pathMatcher, methodMatcher, handler);
    }

    static Route of(
        PathMatcher pathMatcher,
        MethodMatcher methodMatcher,
        Function1Void<RoutingContext, ?> handler) {
        return new RouteImpl(RequestMatcher.any(), pathMatcher, methodMatcher, handler);
    }

    RequestMatcher requestMatcher();

    PathMatcher pathMatcher();

    MethodMatcher methodMatcher();

    Function1Void<RoutingContext, ?> handler();

}
