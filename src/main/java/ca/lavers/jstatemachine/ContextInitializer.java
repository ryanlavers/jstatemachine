package ca.lavers.jstatemachine;

/**
 * A function that can be installed on a StateMachine to initialize
 * the {@link Context} object for each new processing operation.
 *
 * See {@link StateMachineBuilder#setContextInitializer(ContextInitializer)}
 */
@FunctionalInterface
public interface ContextInitializer<T, R> {
    void initialize(Context<T, R> ctx);
}
