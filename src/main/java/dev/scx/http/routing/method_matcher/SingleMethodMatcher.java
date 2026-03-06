package dev.scx.http.routing.method_matcher;

import dev.scx.http.method.ScxHttpMethod;

/// SingleMethodMatcher
///
/// @author scx567888
/// @version 0.0.1
public final class SingleMethodMatcher implements MethodMatcher {

    private final ScxHttpMethod method;

    public SingleMethodMatcher(ScxHttpMethod method) {
        if (method == null) {
            throw new NullPointerException("method must not be null");
        }
        this.method = method;
    }

    @Override
    public boolean matches(ScxHttpMethod method) {
        return this.method.equals(method);
    }

    @Override
    public String toString() {
        return method.value();
    }

    public ScxHttpMethod method() {
        return method;
    }

}
