package dev.scx.http.routing;

import dev.scx.function.Function1Void;
import dev.scx.http.ScxHttpServerRequest;
import dev.scx.http.method.ScxHttpMethod;
import dev.scx.http.routing.method_matcher.MethodMatcher;
import dev.scx.http.routing.path_matcher.PathMatcher;
import dev.scx.http.routing.path_matcher.TemplatePathMatcher;
import dev.scx.http.routing.request_matcher.RequestMatcher;
import dev.scx.http.routing.route_table.PriorityRouteTable;
import dev.scx.http.routing.route_table.RouteTable;
import dev.scx.http.routing.routing_input.RoutingInput;

import java.util.HashMap;
import java.util.function.Consumer;

import static dev.scx.http.method.HttpMethod.*;

/// Router 是一个**便捷入口类**, 用于快速将路由系统接入 HTTP 服务器,
/// 并提供少量用于注册路由的便捷方法.
///
/// ## 定位说明
///
/// `Router` **不是**路由系统的核心抽象, 也 **不定义** 任何路由匹配或分流语义.
/// 路由的实际语义完全由 [Route], [RouteTable] 以及 [RequestMatcher], [PathMatcher], [MethodMatcher] 所决定.
///
/// `Router` 仅作为一种默认的**装配与接入方式**存在,
/// 用于将基于 [RouteTable] 的路由逻辑包装为 HTTP 服务器可直接使用的处理器.
///
/// ## 可替换性
///
/// 该类型被刻意设计为**可替换的工具类**：
/// 调用方完全可以自行实现具有相同行为的入口处理器,
/// 或直接绕过 `Router`, 使用 [RouteTable] 与自定义调度逻辑将路由系统接入服务器.
///
/// 替换或不使用 `Router` **不会**影响路由系统的核心模型, 匹配规则或执行语义.
///
/// ## 使用建议
///
/// - 推荐在应用启动阶段完成路由注册, 然后将 `Router` 实例交给服务器使用.
/// - 如需完全控制路由装配, 生命周期或调度方式, 可不使用 `Router`.
///
/// @author scx567888
/// @version 0.0.1
public final class Router implements Function1Void<ScxHttpServerRequest, Throwable> {

    private final PriorityRouteTable priorityRouteTable;

    public Router() {
        this.priorityRouteTable = PriorityRouteTable.of();
    }

    public static Router of() {
        return new Router();
    }

    public PriorityRouteTable routeTable() {
        return this.priorityRouteTable;
    }

    @Override
    public void apply(ScxHttpServerRequest request) throws Throwable {
        var routingInput = RoutingInput.of(request.path());
        var data = new HashMap<String, Object>();
        RoutingContext.of(this.priorityRouteTable, request, routingInput, data).next();
    }

    //******************* 快捷路由表操作 ******************

    public Router route(int priority, Route route) {
        this.priorityRouteTable.add(priority, route);
        return this;
    }

    public Router route(Route route) {
        this.priorityRouteTable.add(route);
        return this;
    }

    public Router remove(Route route) {
        this.priorityRouteTable.remove(route);
        return this;
    }

    /// 注册一条路由, 匹配给定 HTTP method 与 路径模板.
    public Router route(int priority, ScxHttpMethod method, String template, Function1Void<RoutingContext, ?> handler) {
        return route(priority, Route.of(PathMatcher.ofTemplate(template), MethodMatcher.of(method), handler));
    }

    /// 注册一条路由, 匹配所有 HTTP method 与 路径模板.
    public Router route(int priority, String template, Function1Void<RoutingContext, ?> handler) {
        return route(priority, Route.of(PathMatcher.ofTemplate(template), MethodMatcher.any(), handler));
    }

    /// 注册一条路由, 匹配所有.
    public Router route(int priority, Function1Void<RoutingContext, ?> handler) {
        return route(priority, Route.of(PathMatcher.any(), MethodMatcher.any(), handler));
    }

    /// 注册一条路由, 匹配给定 HTTP method 与 路径模板.
    public Router route(ScxHttpMethod method, String template, Function1Void<RoutingContext, ?> handler) {
        return route(Route.of(PathMatcher.ofTemplate(template), MethodMatcher.of(method), handler));
    }

    /// 注册一条路由, 匹配所有 HTTP method 与 路径模板.
    public Router route(String template, Function1Void<RoutingContext, ?> handler) {
        return route(Route.of(PathMatcher.ofTemplate(template), MethodMatcher.any(), handler));
    }

    /// 注册一条路由, 匹配所有.
    public Router route(Function1Void<RoutingContext, ?> handler) {
        return route(Route.of(PathMatcher.any(), MethodMatcher.any(), handler));
    }

    public Router get(int priority, String template, Function1Void<RoutingContext, ?> handler) {
        return route(priority, GET, template, handler);
    }

    public Router post(int priority, String template, Function1Void<RoutingContext, ?> handler) {
        return route(priority, POST, template, handler);
    }

    public Router put(int priority, String template, Function1Void<RoutingContext, ?> handler) {
        return route(priority, PUT, template, handler);
    }

    public Router delete(int priority, String template, Function1Void<RoutingContext, ?> handler) {
        return route(priority, DELETE, template, handler);
    }

    public Router get(String template, Function1Void<RoutingContext, ?> handler) {
        return route(GET, template, handler);
    }

    public Router post(String template, Function1Void<RoutingContext, ?> handler) {
        return route(POST, template, handler);
    }

    public Router put(String template, Function1Void<RoutingContext, ?> handler) {
        return route(PUT, template, handler);
    }

    public Router delete(String template, Function1Void<RoutingContext, ?> handler) {
        return route(DELETE, template, handler);
    }

    /// 注册一条子路由委托规则.
    ///
    /// `template` 必须是尾部带 `*` 的纯静态模板, 例如 `/*`、`/api/*`.
    /// 不允许包含参数段 如 `:name`.
    ///
    /// 匹配成功后, 会将 `*` 捕获到的剩余路径交给 `childRouter` 继续匹配.
    /// 父子路由共享同一个 request 与 data().
    /// 当剩余路径为空字符串时, 会先归一化为 `/`.
    ///
    /// 子路由中的 `next()` 仅在子路由自身范围内继续,
    /// 不会自动回到父路由继续匹配后续路由.
    public Router mount(int priority, String template, Router childRouter) {
        return route(priority, Route.of(createMountPathMatcher(template), MethodMatcher.any(), new MountedRouterHandler(template, childRouter.priorityRouteTable)));
    }

    public Router mount(String template, Router childRouter) {
        return route(Route.of(createMountPathMatcher(template), MethodMatcher.any(), new MountedRouterHandler(template, childRouter.priorityRouteTable)));
    }

    public Router mount(int priority, String template, Consumer<Router> childRouterBuilder) {
        var childRouter = new Router();
        childRouterBuilder.accept(childRouter);
        return mount(priority, template, childRouter);
    }

    public Router mount(String template, Consumer<Router> childRouterBuilder) {
        var childRouter = new Router();
        childRouterBuilder.accept(childRouter);
        return mount(template, childRouter);
    }

    private TemplatePathMatcher createMountPathMatcher(String template) {
        var templatePathMatcher = PathMatcher.ofTemplate(template);
        if (!templatePathMatcher.hasWildcard()) {
            throw new IllegalArgumentException("mount template must end with '*'");
        }
        if (templatePathMatcher.paramCount() > 0) {
            throw new IllegalArgumentException("mount template must not contain params");
        }
        return templatePathMatcher;
    }

    /// 将 `*` 捕获到的剩余路径转交给子路由继续处理.
    private record MountedRouterHandler(
        String template,
        RouteTable childRouteTable
    ) implements Function1Void<RoutingContext, Throwable> {

        @Override
        public void apply(RoutingContext ctx) throws Throwable {
            var remaining = ctx.pathMatch().capture("*");
            // 子路由捕获是存在 "" 的情况, 这里归一化成 "/" 再分发.
            if (remaining.isEmpty()) {
                remaining = "/";
            }
            RoutingContext.of(childRouteTable, ctx.request(), RoutingInput.of(remaining), ctx.data()).next();
        }

        @Override
        public String toString() {
            return "mount(" + template + ")";
        }

    }

}
