package dev.scx.http.routing;

import dev.scx.http.ScxHttpServerRequest;
import dev.scx.http.routing.path_matcher.PathMatch;
import dev.scx.http.routing.route_table.RouteTable;
import dev.scx.http.routing.routing_input.RoutingInput;

import java.util.Map;

/// RoutingContext
///
/// @author scx567888
/// @version 0.0.1
public interface RoutingContext {

    static RoutingContext of(RouteTable routeTable, ScxHttpServerRequest request, RoutingInput routingInput, Map<String, Object> data) {
        return new RoutingContextImpl(routeTable, request, routingInput, data);
    }

    ScxHttpServerRequest request();

    RoutingInput routingInput();

    PathMatch pathMatch();

    Map<String, Object> data();

    /// 匹配下一个 Route
    void next() throws Throwable;

    default <T extends ScxHttpServerRequest> T request(Class<T> requestType) {
        return requestType.cast(request());
    }

}
