package dev.scx.http.routing.route_table;

import dev.scx.http.routing.Route;
import dev.scx.http.routing.routing_input.RoutingInput;

import java.util.Iterator;

/// RouteTable
///
/// @author scx567888
/// @version 0.0.1
public interface RouteTable {

    /// 返回针对给定 [RoutingInput] 的候选路由序列 (按尝试顺序).
    ///
    /// 实现可以基于 routingInput 进行安全的预筛选或索引优化
    /// (例如前缀树, 哈希表等), 以减少后续匹配开销.
    ///
    /// 但实现 **不得遗漏** 任何可能匹配成功的路由;
    /// 索引与预筛选只能作为性能优化手段, 而不能影响路由语义的正确性.
    Iterator<Route> candidates(RoutingInput routingInput);

}
