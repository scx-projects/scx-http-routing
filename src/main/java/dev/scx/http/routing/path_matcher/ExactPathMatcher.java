package dev.scx.http.routing.path_matcher;

import dev.scx.http.routing.routing_path.RoutingPath;

import static dev.scx.http.routing.path_matcher.EmptyPathMatch.EMPTY_PATH_MATCH;

/// ExactPathMatcher
///
/// @author scx567888
/// @version 0.0.1
public final class ExactPathMatcher implements PathMatcher {

    private final String exactPath;

    public ExactPathMatcher(String exactPath) {
        if (exactPath == null) {
            throw new NullPointerException("exactPath must not be null");
        }
        if (!exactPath.startsWith("/")) {
            throw new IllegalArgumentException("exactPath must start with /");
        }
        this.exactPath = exactPath;
    }

    @Override
    public PathMatch match(RoutingPath path) {
        if (!exactPath.equals(path.value())) {
            return null;
        }
        return EMPTY_PATH_MATCH;
    }

    @Override
    public String toString() {
        return "exact(" + exactPath + ")";
    }

    public String exactPath() {
        return exactPath;
    }

}
