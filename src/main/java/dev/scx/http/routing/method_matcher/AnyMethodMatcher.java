package dev.scx.http.routing.method_matcher;

import dev.scx.http.method.ScxHttpMethod;

/// AnyMethodMatcher
///
/// @author scx567888
/// @version 0.0.1
public final class AnyMethodMatcher implements MethodMatcher {

    public static final AnyMethodMatcher ANY_METHOD_MATCHER = new AnyMethodMatcher();

    /// 保证单例
    private AnyMethodMatcher() {

    }

    @Override
    public boolean matches(ScxHttpMethod method) {
        return true;
    }

    @Override
    public String toString() {
        return "ANY";
    }

}
