package dev.scx.http.routing.test;

import dev.scx.http.routing.path_matcher.PathMatch;
import dev.scx.http.routing.path_matcher.PathMatcher;
import dev.scx.http.routing.routing_path.RoutingPath;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegexPathMatcherTest {

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
    }

    /// 测试正则
    @Test
    public static void test1() {
        PathMatcher pathMatcher = PathMatcher.ofRegex("/users/(\\d+)/books/(\\d+)");
        PathMatch match = pathMatcher.match(RoutingPath.of("/users/123/books/456"));

        Assert.assertEquals(match.capture(0), "123");
        Assert.assertEquals(match.capture(1), "456");
        Assert.assertNull(match.capture(2));
        Assert.assertNull(match.capture("id"));
    }

    /// 命名捕获 + index
    @Test
    public static void test2() {
        PathMatcher pathMatcher =
            PathMatcher.ofRegex("/users/(?<userId>\\d+)/books/(?<bookId>\\d+)");
        PathMatch match = pathMatcher.match(RoutingPath.of("/users/42/books/99"));

        Assert.assertEquals(match.capture(0), "42");
        Assert.assertEquals(match.capture(1), "99");

        Assert.assertEquals(match.capture("userId"), "42");
        Assert.assertEquals(match.capture("bookId"), "99");
        Assert.assertNull(match.capture("xxx"));
    }

    /// 命名 + 非命名混合，验证顺序
    @Test
    public static void test3() {
        PathMatcher pathMatcher =
            PathMatcher.ofRegex("/(?<lang>[a-z]{2})/users/(\\d+)/posts/(?<postId>\\d+)");
        PathMatch match = pathMatcher.match(RoutingPath.of("/en/users/123/posts/456"));

        Assert.assertEquals(match.capture(0), "en");
        Assert.assertEquals(match.capture(1), "123");
        Assert.assertEquals(match.capture(2), "456");

        Assert.assertEquals(match.capture("lang"), "en");
        Assert.assertEquals(match.capture("postId"), "456");
    }

    /// 可选捕获（存在但为 null）
    @Test
    public static void test4() {
        PathMatcher pathMatcher =
            PathMatcher.ofRegex("/users/(?<id>\\d+)(?:/posts/(?<postId>\\d+))?");
        PathMatch match = pathMatcher.match(RoutingPath.of("/users/123"));

        Assert.assertEquals(match.capture(0), "123");
        Assert.assertNull(match.capture(1));

        Assert.assertEquals(match.capture("id"), "123");
        Assert.assertNull(match.capture("postId"));
    }

    /// 可选捕获（第二种路径）
    @Test
    public static void test5() {
        PathMatcher pathMatcher =
            PathMatcher.ofRegex("/users/(?<id>\\d+)(?:/posts/(?<postId>\\d+))?");
        PathMatch match = pathMatcher.match(RoutingPath.of("/users/123/posts/456"));

        Assert.assertEquals(match.capture(0), "123");
        Assert.assertEquals(match.capture(1), "456");

        Assert.assertEquals(match.capture("id"), "123");
        Assert.assertEquals(match.capture("postId"), "456");
    }

    /// 非匹配情况
    @Test
    public static void test6() {
        PathMatcher pathMatcher = PathMatcher.ofRegex("/users/(\\d+)");
        PathMatch match = pathMatcher.match(RoutingPath.of("/users/abc"));

        Assert.assertNull(match);
    }

    /// 验证 matches() 是全匹配
    @Test
    public static void test7() {
        PathMatcher pathMatcher = PathMatcher.ofRegex("/users/(\\d+)");
        PathMatch match = pathMatcher.match(RoutingPath.of("/foo/users/123/bar"));

        Assert.assertNull(match);
    }

}
