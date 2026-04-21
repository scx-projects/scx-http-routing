package dev.scx.http.routing.test;

import dev.scx.http.method.ScxHttpMethod;
import dev.scx.http.routing.method_matcher.MethodMatcher;
import org.testng.Assert;
import org.testng.annotations.Test;

import static dev.scx.http.method.HttpMethod.GET;
import static dev.scx.http.method.HttpMethod.POST;

public class MethodMatcherTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        var matcher = MethodMatcher.of(GET, POST);
        var result = matcher.matches(ScxHttpMethod.of("GET"));
        Assert.assertEquals(result, true);
    }

}
