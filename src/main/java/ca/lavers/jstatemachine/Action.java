package ca.lavers.jstatemachine;

/**
 * An action that can be executed by a {@link StateMachine} when a rule matches.
 *
 * @param <T> The type of tokens in the input stream
 * @param <R> The type of tokens to be returned in the output stream
 */
@FunctionalInterface
public interface Action<T, R> {
    /**
     * Execute this action.
     * @param ctx The Context object for the current operation
     */
    void execute(Context<T, R> ctx);
}
