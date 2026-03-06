package dev.scx.http.routing.path_matcher;

import dev.scx.http.routing.routing_path.RoutingPath;

import static dev.scx.http.routing.path_matcher.EmptyPathMatch.EMPTY_PATH_MATCH;

/// AnyPathMatcher
///
/// @author scx567888
/// @version 0.0.1
public final class AnyPathMatcher implements PathMatcher {

    public static final AnyPathMatcher ANY_PATH_MATCHER = new AnyPathMatcher();

    /// 保证单例
    private AnyPathMatcher() {

    }

    @Override
    public PathMatch match(RoutingPath path) {
        return EMPTY_PATH_MATCH;
    }

    @Override
    public String toString() {
        return "ANY";
    }

}
