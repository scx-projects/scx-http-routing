package dev.scx.http.routing.path_matcher;

import java.util.Map;

/// IndexedPathMatch
///
/// @author scx567888
/// @version 0.0.1
public final class IndexedPathMatch implements PathMatch {

    private final String[] values;
    private final Map<String, Integer> nameToIndex;
    private Map<String, String> namedCaptures;

    public IndexedPathMatch(String[] values, Map<String, Integer> nameToIndex) {
        this.values = values;
        this.nameToIndex = nameToIndex;
        this.namedCaptures = null;
    }

    @Override
    public String capture(int index) {
        // 不在范围内.
        if (index < 0 || index >= values.length) {
            return null;
        }
        return values[index];
    }

    @Override
    public String capture(String name) {
        var i = nameToIndex.get(name);
        if (i == null) {
            return null;
        }
        return capture(i);
    }

    @Override
    public int captureCount() {
        return values.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> namedCaptures() {
        // 懒加载 + 不可变
        if (namedCaptures == null) {
            var entries = new Map.Entry[nameToIndex.size()];
            var i = 0;
            for (var e : nameToIndex.entrySet()) {
                entries[i] = Map.entry(e.getKey(), values[e.getValue()]);
                i = i + 1;
            }
            namedCaptures = Map.ofEntries(entries);
        }
        return namedCaptures;
    }

}
