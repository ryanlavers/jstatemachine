package ca.lavers.jstatemachine;

/**
 * A Matcher function is executed each time an .on() rule for which it is defined
 * is considered. If the matcher returns true, that rule is chosen to be executed.
 *
 * Generally the matcher will examine {@link Context#currentItem()} to decide if
 * the rule should execute, but can consider any other available context information,
 * such as attributes, to implement its functionality.
 *
 * @param <T> The type of tokens in the input stream
 * @param <R> The type of tokens to be returned in the output stream
 */
@FunctionalInterface
public interface Matcher<T, R> {
    boolean matches(Context<T, R> ctx);
}
