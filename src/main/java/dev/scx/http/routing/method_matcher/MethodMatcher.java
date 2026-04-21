package dev.scx.http.routing.method_matcher;

import dev.scx.http.method.ScxHttpMethod;

import static dev.scx.http.routing.method_matcher.AnyMethodMatcher.ANY_METHOD_MATCHER;

/// MethodMatcher
///
/// @author scx567888
/// @version 0.0.1
public interface MethodMatcher {

    static MethodMatcher any() {
        return ANY_METHOD_MATCHER;
    }

    static MethodMatcher of(ScxHttpMethod... methods) {
        if (methods == null) {
            throw new NullPointerException("methods must not be null");
        }
        if (methods.length == 0) {
            throw new IllegalArgumentException("methods must not be empty");
        }
        if (methods.length == 1) {
            return new SingleMethodMatcher(methods[0]);
        }
        return new MultiMethodMatcher(methods);
    }

    boolean matches(ScxHttpMethod method);

}
