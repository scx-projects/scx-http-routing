package dev.scx.http.routing;

import dev.scx.http.ScxHttpServerRequest;
import dev.scx.http.exception.HttpException;
import dev.scx.http.exception.MethodNotAllowedException;
import dev.scx.http.exception.NotFoundException;
import dev.scx.http.routing.path_matcher.PathMatch;
import dev.scx.http.routing.route_table.RouteTable;
import dev.scx.http.routing.routing_input.RoutingInput;

import java.util.Iterator;
import java.util.Map;

/// RoutingContextImpl
///
/// @author scx567888
/// @version 0.0.1
final class RoutingContextImpl implements RoutingContext {

    private final Iterator<Route> routeIterator;
    private final ScxHttpServerRequest request;
    private final RoutingInput routingInput;
    private final Map<String, Object> data;
    private PathMatch nowPathMatch;

    public RoutingContextImpl(RouteTable routeTable, ScxHttpServerRequest request, RoutingInput routingInput, Map<String, Object> data) {
        this.routeIterator = routeTable.candidates(routingInput);
        this.request = request;
        this.routingInput = routingInput;
        this.data = data;
    }

    @Override
    public ScxHttpServerRequest request() {
        return request;
    }

    @Override
    public RoutingInput routingInput() {
        return routingInput;
    }

    @Override
    public PathMatch pathMatch() {
        return nowPathMatch;
    }

    @Override
    public Map<String, Object> data() {
        return data;
    }

    /// 任何路径都不匹配 抛出 404.
    /// 存在路径匹配, 但是任何方法都不匹配 抛出 405.
    @Override
    public void next() throws Throwable {
        HttpException e = new NotFoundException();

        while (routeIterator.hasNext()) {
            var route = routeIterator.next();

            // 1, 先处理 通用匹配器
            var requestMatched = route.requestMatcher().matches(request);

            if (!requestMatched) {
                continue;
            }

            // 2, 然后匹配路径
            var pathMatch = route.pathMatcher().match(routingInput.path());

            // 匹配不到就跳到下一个路由
            if (pathMatch == null) {
                continue;
            }

            this.nowPathMatch = pathMatch;

            // 3, 最后匹配方法
            var methodMatched = route.methodMatcher().matches(request.method());

            // 匹配方法失败.
            // 这里只记录而不直接抛出异常, 因为后续可能其他路由会匹配成功.
            if (!methodMatched) {
                e = new MethodNotAllowedException();
                continue;
            }

            route.handler().apply(this);

            return;

        }

        throw e;
    }

}
