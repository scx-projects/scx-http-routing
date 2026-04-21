package dev.scx.http.routing.route_table;

import dev.scx.http.routing.Route;

import java.util.List;

/// 优先级匹配 数字越小优先级越高.
///
/// @author scx567888
/// @version 0.0.1
public interface PriorityRouteTable extends RouteTable {

    static PriorityRouteTable of() {
        return new PriorityRouteTableImpl();
    }

    /// 添加一个路由
    ///
    /// @param priority 路由优先级: 数值越小越先匹配, 相同 priority 按注册顺序匹配.
    PriorityRouteTable add(int priority, Route route);

    /// 移除所有使用给定 Route 实例注册的路由条目 (按对象身份 == 匹配, 不使用 equals).
    PriorityRouteTable remove(Route route);

    /// 只读快照
    List<PriorityRouteEntry> entries();

    /// 添加一个路由, 默认 priority = 0.
    default PriorityRouteTable add(Route route) {
        return add(0, route);
    }

}
