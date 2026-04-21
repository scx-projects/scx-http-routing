package dev.scx.http.routing.path_matcher;

import java.util.Map;

/// EmptyPathMatch
///
/// @author scx567888
/// @version 0.0.1
public final class EmptyPathMatch implements PathMatch {

    public static final EmptyPathMatch EMPTY_PATH_MATCH = new EmptyPathMatch();

    @Override
    public String capture(int index) {
        // 什么都不匹配 返回 null
        return null;
    }

    @Override
    public String capture(String name) {
        // 什么都不匹配 返回 null
        return null;
    }

    @Override
    public int captureCount() {
        return 0;
    }

    @Override
    public Map<String, String> namedCaptures() {
        return Map.of();
    }

}
