package dev.scx.http.routing.route_table;

import dev.scx.http.routing.Route;
import dev.scx.http.routing.routing_input.RoutingInput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/// PriorityRouteTableImpl
///
/// @author scx567888
/// @version 0.0.1
final class PriorityRouteTableImpl implements PriorityRouteTable {

    private final ArrayList<PriorityRouteEntry> entries;

    public PriorityRouteTableImpl() {
        this.entries = new ArrayList<>();
    }

    @Override
    public PriorityRouteTableImpl add(int priority, Route route) {
        var routeEntry = new PriorityRouteEntry(priority, route);
        int idx = upperBound(routeEntry.priority()); // 计算索引.
        entries.add(idx, routeEntry); // 插到相同 priority 段的末尾
        return this;
    }

    @Override
    public PriorityRouteTableImpl remove(Route route) {
        entries.removeIf(c -> c.route() == route);
        return this;
    }

    @Override
    public List<PriorityRouteEntry> entries() {
        return List.copyOf(entries);
    }

    @Override
    public Iterator<Route> candidates(RoutingInput routingInput) {
        // 这里我们忽略 routingInput, 直接返回全量.
        return new RouteIterator(entries.iterator());
    }

    /// 二分法查找, 返回第一个 entry.priority() > priority 的位置 (upper bound).
    /// 这样相同 priority 的新 route 会插到已有相同 priority 的后面, 保持注册顺序.
    private int upperBound(int priority) {
        int lo = 0;
        int hi = entries.size();
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            int midPriority = entries.get(mid).priority();
            if (midPriority <= priority) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    /// 迭代器
    private record RouteIterator(Iterator<PriorityRouteEntry> iterator) implements Iterator<Route> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Route next() {
            return iterator.next().route();
        }

    }

}
