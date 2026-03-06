package dev.scx.http.routing.path_matcher;

import dev.scx.http.routing.routing_path.RoutingPath;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/// RegexPathMatcher
///
/// @author scx567888
/// @version 0.0.1
public final class RegexPathMatcher implements PathMatcher {

    private final Pattern pattern;
    private final Map<String, Integer> nameToIndex;

    public RegexPathMatcher(Pattern pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern must not be null");
        }
        this.pattern = pattern;
        this.nameToIndex = createNameToIndex(this.pattern);
    }

    private static Map<String, Integer> createNameToIndex(Pattern pattern) {
        // 将 1-based 的 namedGroups 转换成 0-based 的 nameToIndex
        var namedGroups = pattern.namedGroups();

        var nameToIndex = new HashMap<String, Integer>();

        for (var e : namedGroups.entrySet()) {
            var groupNumber = e.getValue();     // 1-based
            nameToIndex.put(e.getKey(), groupNumber - 1); // 转成 0-based
        }
        // 保证不可变.
        return Map.copyOf(nameToIndex);
    }

    @Override
    public PathMatch match(RoutingPath path) {
        var matcher = pattern.matcher(path.value());
        if (!matcher.matches()) {
            return null;
        }

        var values = new String[matcher.groupCount()];

        for (int i = 0; i < values.length; i = i + 1) {
            values[i] = matcher.group(i + 1); // 注意 group 索引是 1 起始.
        }

        // nameToIndex 是不可变的 Map 这里可以安全传递.
        return new IndexedPathMatch(values, nameToIndex);
    }

    @Override
    public String toString() {
        return "regex(" + pattern.pattern() + ")";
    }

    public Pattern pattern() {
        return pattern;
    }

}
