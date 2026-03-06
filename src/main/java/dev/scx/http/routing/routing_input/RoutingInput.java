package dev.scx.http.routing.routing_input;

import dev.scx.http.routing.routing_path.RoutingPath;

/// RoutingInput 表示路由系统在进行匹配与分流时所使用的 "输入视图".
///
/// 该抽象刻意只建模 URI path (不包含 query),
/// 用于支持路由组合场景, 例如子路由, 路径前缀处理以及路径重写等.
///
/// query 属于原始请求 URI 的事实属性, 不作为可重写的路由视图.
/// 如需读取或匹配 query, 应通过 request.uri() 或 RequestMatcher 完成.
///
/// 虽然从理论上讲, HTTP headers 和 method 也可以作为路由视图的一部分,
/// 但在本设计中被有意识地排除:
///
/// - headers 被视为原始请求 (request) 的事实属性,
///   通过 `RequestMatcher` 在事实层面进行匹配,
///   而不是作为可重写的路由视图.
///
/// - HTTP method 的重写在实际 Web 路由场景中意义有限,
///   因此不作为 RoutingInput 的建模内容.
///
/// 该取舍旨在保持 `RoutingInput` 的职责单一和语义清晰,
/// 避免其演化为另一个 request 抽象,
/// 同时仍然覆盖路由系统中唯一具有普遍实际价值的视图变换能力: 路径(path)重写.
///
/// @author scx567888
/// @version 0.0.1
public interface RoutingInput {

    static RoutingInput of(RoutingPath path) {
        return new RoutingInputImpl(path);
    }

    static RoutingInput of(String path) {
        return of(RoutingPath.of(path));
    }

    RoutingPath path();

}
