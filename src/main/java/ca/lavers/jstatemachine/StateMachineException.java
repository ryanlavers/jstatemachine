package ca.lavers.jstatemachine;

/**
 * Thrown from the output stream's methods when the StateMachine encounters
 * an error(message) action. Includes the original error message, position of
 * the error in the original input stream, and provides access to the Context
 * object at the point of the error.
 */
public class StateMachineException extends RuntimeException {
    protected final Context<?, ?> ctx;
    protected String originalMessage;

    public StateMachineException(String message, Context<?, ?> ctx) {
        // TODO: Needed? If someone wants the position they can get with getPosition()
        super(message + (ctx.currentPosition() >= 0 ? " at position " + ctx.currentPosition() : ""));
        this.ctx = ctx;
        this.originalMessage = message;
    }

    public Context<?, ?> getContext() {
        return ctx;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public int getPosition() {
        return ctx.currentPosition();
    }
}
