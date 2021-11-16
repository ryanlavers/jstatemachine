package ca.lavers.jstatemachine;

import java.util.*;

/**
 * A built state machine, ready to process some streams. Despite the name, instances
 * of this class are stateless -- multiple executions of {@link #process(TokenStream)}
 * are permitted, even concurrently.
 *
 * Not meant to be instantiated directly; use {@link StateMachineBuilder}.
 *
 * @param <T> The type of tokens in the input stream
 * @param <R> The type of tokens to be returned in the output stream
 */
public class StateMachine<T, R> {

    private final ContextInitializer<T, R> ctxInit;
    private final Map<String, State<T, R>> states;
    private final String initialState;

    /**
     * Used by {@link StateMachineBuilder} to construct a StateMachine
     */
    StateMachine(Map<String, State<T, R>> states, String initialState, ContextInitializer<T, R> ctxInit) {
        this.states = states;
        this.initialState = initialState;
        this.ctxInit = ctxInit;
    }

    /**
     * Begin processing a {@link TokenStream}. Each item read from the input will be
     * tested against the rules defined in the current state, and any actions
     * associated with the chosen rule will be executed. Any items emitted by the
     * state machine will be written to the returned stream.
     *
     * Multiple streams can be processed with the same StateMachine, even concurrently,
     * as all state for a given operation is contained within the returned stream.
     *
     * @param stream Stream of input tokens
     * @return A stream of tokens as emitted by this state machine
     * @throws StateMachineException when an error(message) action is executed
     */
    public TokenStream<R> process(TokenStream<T> stream) throws StateMachineException {
        return new TokenStream<R>() {
            final Context<T, R> ctx = new Context<>(initialState);
            final Queue<R> outputBuffer = new LinkedList<>();
            boolean finished = false;

            { ctxInit.initialize(ctx); }

            private void tryGenerateOutput() {
                while(!finished && !ctx.isFailed() && outputBuffer.isEmpty()) {
                    Optional<T> input = stream.next();
                    if (input.isPresent()) {
                        outputBuffer.addAll(processItem(ctx, input.get()));
                    } else {
                        outputBuffer.addAll(finish(ctx));
                        finished = true;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                tryGenerateOutput();
                if(outputBuffer.isEmpty()) {
                    if(ctx.isFailed()) {
                        throw ctx.getThrownException();
                    }
                    return false;
                }
                return true;
            }

            @Override
            public Optional<R> next() {
                tryGenerateOutput();
                if(outputBuffer.isEmpty()) {
                    if(ctx.isFailed()) {
                        throw ctx.getThrownException();
                    }
                    return Optional.empty();
                }
                return Optional.of(outputBuffer.remove());
            }
        };
    }

    /**
     * Process a single input item with the given Context, returning all output items generated.
     */
    private List<R> processItem(Context<T, R> ctx, T item) throws StateMachineException {
        ctx.setCurrentItem(item);
        ctx.incrementPosition();
        State<T, R> state = states.get(ctx.currentState());
        try {
            for (Action<T, R> action : state.actionsFor(ctx)) {
                action.execute(ctx);
            }
        } catch(StateMachineException e) {
            ctx.setError(e);
        }
        return ctx.getOutputBufferAndClear();
    }

    /**
     * Executes the actions in the current state's onEnd() rule, returning all generated output items.
     */
    private List<R> finish(Context<T, R> ctx) throws StateMachineException {
        ctx.setCurrentItem(null);
        // TODO - Maybe don't clear position so that emitted items can still have a reasonable position tag (when implemented)
        ctx.clearPosition();
        State<T, R> state = states.get(ctx.currentState());
        try {
            for (Action<T, R> action : state.getEndActions()) {
                action.execute(ctx);
            }
        } catch(StateMachineException e) {
            ctx.setError(e);
        }
        return ctx.getOutputBufferAndClear();
    }


}
