package dev.scx.http.routing.routing_path;

/// 所有 path 相关均是已解码之后的.
/// segment 指的是 根据 / 进行切割 (包含空段).
///
/// @author scx567888
/// @version 0.0.1
public interface RoutingPath {

    static RoutingPath of(String path) {
        return new RoutingPathImpl(path);
    }

    String value();

    int segmentCount();

    String segment(int index);

}
