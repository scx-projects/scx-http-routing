package dev.scx.http.routing.path_matcher;

import dev.scx.http.routing.routing_path.RoutingPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// `TemplatePathMatcher` 基于简洁, 显式的路径模板语法实现路径匹配.
///
/// ## 模板语法
///
/// 模板必须以 `/` 开头, 由 `/` 分隔的若干段 (segment) 组成.
///
/// 支持以下三种段类型:
///
/// - **静态段**: 普通字符串, 要求与请求路径中的对应段完全相等.
/// - **参数段**: `:name`, 匹配恰好一个路径段, 并以 `name` 为键捕获其值.
/// - **通配段**: `*`, 匹配剩余的零个或多个路径段. 若存在通配段, 则**必须是模板的最后一段**.
///
/// ## 匹配规则
///
///  - 请求路径必须以 `/` 开头.
///  - 静态段必须严格相等.
///  - 参数段匹配且仅匹配一个路径段.
///  - 通配段 `*` 可匹配零个或多个后续路径段.
///  - 若模板中不包含 `*`, 则请求路径段数量必须与模板段数量完全一致.
///
/// ## 捕获语义
/// 匹配成功后, 可通过 [PathMatch#capture(String)] 或 [PathMatch#capture(int)] 获取捕获值.
///
/// - 每个 `:name` 参数捕获对应的路径段字符串.
/// - `*` 捕获请求路径中尚未匹配的剩余部分, 捕获值是**原始请求路径的子串**, 不会做任何归一化处理.
///
/// ### 通配段捕获示例
///
/// 对于模板 `/api/*`:
///
/// ```
/// /api        -> "*" = ""
/// /api/       -> "*" = "/"
/// /api/user   -> "*" = "/user"
/// /api/a/b    -> "*" = "/a/b"
/// ```
///
/// 该设计会刻意保留尾部斜杠与空段信息,
/// 使调用方能够区分 `""`, `"/"` 与 `"/rest"`.
/// 如需宽松或归一化行为, 应由更高层路由逻辑自行处理.
///
/// ## 使用场景
///
/// `TemplatePathMatcher` 常用于路由与子路由匹配.
/// 典型用法是使用 `/base/*` 挂载子路由, 并通过
/// `capture("*")` 获取剩余路径, 交由子路由继续处理.
///
/// 本类刻意采用严格, 可预测的匹配语义, 不包含隐式规则或自动修正行为.
///
/// @author scx567888
/// @version 0.0.1
public final class TemplatePathMatcher implements PathMatcher {

    private final String template;
    private final Token[] tokens;
    private final Map<String, Integer> nameToIndex;
    private final int paramCount;
    private final boolean hasWildcard;

    public TemplatePathMatcher(String template) {
        if (template == null) {
            throw new NullPointerException("template must not be null");
        }
        if (!template.startsWith("/")) {
            throw new IllegalArgumentException("template must start with /");
        }
        this.template = template;
        this.tokens = templateToToken(this.template);
        this.nameToIndex = createNameToIndex(this.tokens);
        this.paramCount = computeParamCount(this.tokens);
        this.hasWildcard = computeHasWildcard(this.tokens);
    }

    private static Token[] templateToToken(String template) {
        var segments = template.split("/", -1);

        // 我们需要从 segments 中丢弃第一段, 因为这是来自 split 的副作用.
        var tokens = new Token[segments.length - 1];
        for (int i = 1; i < segments.length; i = i + 1) {
            var segment = segments[i];
            Token token;
            if (segment.equals("*")) {
                // "*" 必须是最后一段.
                if (i != segments.length - 1) {
                    throw new IllegalArgumentException("'*' must be last");
                }
                token = WildcardToken.WILDCARD_TOKEN;
            } else if (segment.startsWith(":")) {
                // 参数合法性校验.
                var name = segment.substring(1);
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("param name must not be empty");
                }
                if ("*".equals(name)) {
                    throw new IllegalArgumentException("param name can not be '*'");
                }
                token = new ParamToken(name);
            } else {
                // 静态段
                token = new StaticToken(segment);
            }
            tokens[i - 1] = token;
        }
        return tokens;
    }

    /// 将 tokens 转换成 nameToIndex, 同时进行 ParamToken 的命名冲突校验.
    private static Map<String, Integer> createNameToIndex(Token[] tokens) {
        var nameToIndex = new HashMap<String, Integer>();

        var index = 0;
        for (var token : tokens) {
            switch (token) {
                case StaticToken s -> {
                    // 直接跳过.
                }
                case ParamToken p -> {
                    String name = p.name();
                    // 重复 参数名 校验.
                    if (nameToIndex.putIfAbsent(name, index) != null) {
                        throw new IllegalArgumentException("duplicate param name: " + name);
                    }
                    index += 1;
                }
                case WildcardToken w -> {
                    nameToIndex.put("*", index);
                    index += 1;
                }
            }

        }
        // 保证不可变.
        return Map.copyOf(nameToIndex);
    }

    private static int computeParamCount(Token[] tokens) {
        var i = 0;
        for (Token token : tokens) {
            if (token instanceof ParamToken) {
                i = i + 1;
            }
        }
        return i;
    }

    private static boolean computeHasWildcard(Token[] tokens) {
        return tokens[tokens.length - 1] == WildcardToken.WILDCARD_TOKEN;
    }

    @Override
    public PathMatch match(RoutingPath path) {

        // 1, 长度校验.
        int fixedTokenCount = hasWildcard ? tokens.length - 1 : tokens.length;

        // 如果包含尾部通配符. 长度必须大于等于固定 token 长度.
        if (hasWildcard) {
            if (path.segmentCount() < fixedTokenCount) {
                return null;
            }
        } else { // 没有通配符 长度必须相等
            if (path.segmentCount() != fixedTokenCount) {
                return null;
            }
        }

        // 2, 逐段匹配.

        // 参数
        var values = new String[hasWildcard ? paramCount + 1 : paramCount];

        var pathPosition = 0;
        var valueIndex = 0;
        int tokenIndex = 0;

        while (tokenIndex < fixedTokenCount) {
            var token = tokens[tokenIndex];
            var segment = path.segment(tokenIndex);
            switch (token) {
                // 必须严格相等
                case StaticToken s -> {
                    if (!s.value().equals(segment)) {
                        // 终止匹配
                        return null;
                    }
                }
                case ParamToken p -> {
                    values[valueIndex] = segment;
                    // 移动参数 索引
                    valueIndex = valueIndex + 1;
                }
                // 这里理论上不可能发生
                case WildcardToken w -> throw new IllegalStateException("WildcardToken must be the last token");
            }
            // 移动指针
            pathPosition += 1 + segment.length();
            // 移动 token 索引
            tokenIndex = tokenIndex + 1;
        }

        if (hasWildcard) {
            values[valueIndex] = path.value().substring(pathPosition);
        }

        return new IndexedPathMatch(values, nameToIndex);
    }

    @Override
    public String toString() {
        return "template(" + template + ")";
    }

    /// 返回 token 列表, 用于高级用法 比如权重计算, 冲突检测等.
    public List<Token> tokens() {
        return List.of(tokens);
    }

    /// 模板字符串
    public String template() {
        return template;
    }

    /// 参数数量
    public int paramCount() {
        return paramCount;
    }

    /// 是否存在通配段
    public boolean hasWildcard() {
        return hasWildcard;
    }

    public sealed interface Token permits StaticToken, ParamToken, WildcardToken {

    }

    public record StaticToken(String value) implements Token {

    }

    public record ParamToken(String name) implements Token {

    }

    public record WildcardToken() implements Token {
        static final WildcardToken WILDCARD_TOKEN = new WildcardToken();
    }

}
