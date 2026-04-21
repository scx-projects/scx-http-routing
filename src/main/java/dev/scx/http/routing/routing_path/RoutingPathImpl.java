package dev.scx.http.routing.routing_path;

/// RoutingPathImpl
///
/// @author scx567888
/// @version 0.0.1
final class RoutingPathImpl implements RoutingPath {

    private final String value;
    private final String[] segments;

    public RoutingPathImpl(String value) {
        this.value = value;
        // 使用 split("/", -1) 以保留尾部空段（如 "/a/"）,
        // 且由于 path 以 '/' 开头, segments[0] 恒为 "",
        // 因此第 i 个逻辑段对应 segments[i + 1].
        this.segments = value.split("/", -1);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public int segmentCount() {
        return segments.length - 1;
    }

    @Override
    public String segment(int index) {
        return segments[index + 1];
    }

}
