package ca.lavers.jstatemachine;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of built-in actions for {@link StateMachine}s.
 */
public class Actions {

    /**
     * Changes the current state of the state machine. Note that while {@link Context#currentState()}
     * will reflect the change immediately, the rest of the actions defined for the current rule
     * will still execute. The next input item, however, will be processed against the rules defined
     * in the new state.
     *
     * @param name The name of the state to switch to
     */
    public static <T, R> Action<T, R> state(String name) {
        return ctx -> {
            ctx.setCurrentState(name);
        };
    }

    /**
     * Causes processing to immediately halt with an error message. No more actions will execute,
     * and no more items will be read from the input stream. The error will be thrown as a
     * {@link StateMachineException} by any attempt to read from the output stream. Any further
     * attempts to read from the stream will just re-throw the exception.
     *
     * @param message The error message to emit
     */
    public static <T, R> Action<T, R> error(String message) {
        return ctx -> {
            throw new StateMachineException(message, ctx);
        };
    }

    public static <T, R> Action<T, R> call(String state) {
        return ctx -> {
            List<String> callstack = ctx.get("callstack", List.class);
            if(callstack == null) {
                callstack = new ArrayList<>();
                ctx.put("callstack", callstack);
            }
            callstack.add(ctx.currentState());
            ctx.setCurrentState(state);
        };
    }

    public static <T, R> Action<T, R> ret() {
        return ctx -> {
            List<String> callstack = ctx.get("callstack", List.class);
            if (callstack == null || callstack.isEmpty()) {
                throw new StateMachineException("Cannot return; missing or empty callstack", ctx);
            }
            ctx.setCurrentState(callstack.remove(callstack.size() - 1));
        };
    }
}
