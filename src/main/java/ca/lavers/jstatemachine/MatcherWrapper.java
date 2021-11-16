package ca.lavers.jstatemachine;

/**
 * A function that can be installed on a StateMachine that will wrap any matchers in its configured
 * rules that aren't already {@link Matcher}s.
 *
 * See {@link StateMachineBuilder#setMatcherWrapper(MatcherWrapper)}
 */
@FunctionalInterface
public interface MatcherWrapper<T, R> {
    Matcher<T, R> wrap(Object o);
}
