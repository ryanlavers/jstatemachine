package ca.lavers.jstatemachine;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures and constructs instances of {@link StateMachine}.
 *
 * @param <T> The type of tokens in the input stream
 * @param <R> The type of tokens to be returned in the output stream
 */
public class StateMachineBuilder<T, R> {

    private Map<String, State<T, R>> states = new HashMap<>();
    private String initialState;
    private ContextInitializer<T, R> contextInitializer = ctx -> {};

    // Default MatcherWrapper just checks object equality (via .equals())
    private MatcherWrapper<T, R> matcherWrapper = m -> (ctx) -> ctx.currentItem().equals(m);

    /**
     * Supply a custom wrapper function that will be called to convert each matcher
     * object (the first argument to an .on() rule) to a {@link Matcher}. The default
     * wrapper just checks if the supplied object .equals() the current input item.
     *
     * Note that this wrapper will only be used when a non-{@link Matcher} is provided
     * as the first argument to an .on() rule.
     *
     * @param wrapper The wrapper to use
     */
    // TODO -- Ability to wrap the existing wrapper? Or at least get it
    public StateMachineBuilder<T, R> setMatcherWrapper(MatcherWrapper<T, R> wrapper) {
        this.matcherWrapper = wrapper;
        return this;
    }

    /**
     * Supply a function that will be called to initialize the {@link Context} object
     * (if needed) at the beginning of each stream processing operation. This can be
     * used to set initial values for context attributes, if custom actions require it.
     *
     * @param initializer The context initializer function to use
     */
    // TODO -- Ability to wrap the existing initializer? Or at least get it
    public StateMachineBuilder<T, R> setContextInitializer(ContextInitializer<T, R> initializer) {
        this.contextInitializer = initializer;
        return this;
    }

    /**
     * Start defining a new state. All rule definitions (.on(), .onEnd(), .otherwise()) following this
     * call will apply to this state, until the next .state() call. The first state defined will
     * also be the initial state of the StateMachine.
     *
     * @param name The name of the new state
     */
    public StateBuilder state(String name) {
        State<T, R> state = states.computeIfAbsent(name, n -> new State<T, R>());
        if(initialState == null) {
            initialState = name;
        }
        return new StateBuilder(state);
    }

    /**
     * Constructs the {@link StateMachine} as configured.
     * @return The new StateMachine
     */
    public StateMachine<T, R> build() {
        // TODO -- Actually clone the states properly. Someone might still have a StateBuilder.
        Map<String, State<T, R>> s = states;
        states = new HashMap<>();
        return new StateMachine<T, R>(s, initialState, contextInitializer);
    }

    /**
     * Builder for a single State
     */
    public class StateBuilder {
        private final State<T, R> state;

        private StateBuilder(State<T, R> state) {
            this.state = state;
        }

        /**
         * Define a rule in the current state that will execute if the supplied matcher
         * matches the current input item (strictly speaking, the matcher can use anything
         * in the current context to decide if the rule should match, but generally will
         * be looking at the current input item). If this rule matches, then the associated
         * actions will be executed.
         *
         * Rules are examined in the order they are defined and only the first matching rule
         * in a state will be executed.
         *
         * @param matcher The matcher function that will decide if this rule should be executed
         * @param actions The actions that will be executed, in order, if this rule matches
         */
        @SafeVarargs
        public final StateBuilder on(Matcher<T, R> matcher, Action<T, R>... actions) {
            state.addRule(matcher, actions);
            return this;
        }

        /**
         * Define a rule in the current state that will execute if the supplied matcher
         * object matches the current input item. If this rule matches, then the associated
         * actions will be executed.
         *
         * The matcher object will be turned into a {@link Matcher} by the matcher wrapper
         * function defined by {@link #setMatcherWrapper(MatcherWrapper)} and then this
         * method behaves the same way as {@link #on(Matcher, Action[])}. The default
         * matcher wrapper simply compares the given object with the current input item
         * using .equals()
         *
         * @param matcher An object to compare against the current input item to determine
         *                if this rule should match
         * @param actions The actions that will be executed, in order, if this rule matches
         */
        @SafeVarargs
        public final StateBuilder on(Object matcher, Action<T, R>... actions) {
            state.addRule(matcherWrapper.wrap(matcher), actions);
            return this;
        }

        /**
         * Define a rule in the current state that will execute once there are no more
         * items in the input stream to process. Rules defined with .on() and .otherwise()
         * are not considered in this case, so it doesn't matter if this call comes before
         * or after other rules. There may only be one .onEnd() rule per state; multiple
         * calls will just overwrite the previous.
         *
         * @param actions The actions that will be executed, in order, if this rule matches
         */
        @SafeVarargs
        public final StateBuilder onEnd(Action<T, R>... actions) {
            state.setEndActions(actions);
            return this;
        }

        /**
         * Define a rule in the current state that will execute if no other rules in this
         * state match. Since this happens after all other rules are considered, it doesn't
         * matter if this call comes before or after other rules. There may only be one
         * .otherwise() rule per state; multiple calls will just overwrite the previous.
         *
         * @param actions The actions that will be executed, in order, if this rule matches
         */
        @SafeVarargs
        public final StateBuilder otherwise(Action<T, R>... actions) {
            state.setOtherwiseActions(actions);
            return this;
        }

        /**
         * Returns the parent {@link StateMachineBuilder} of this StateBuilder
         */
        public StateMachineBuilder<T, R> builder() {
            return StateMachineBuilder.this;
        }

        /**
         * Convenience method to begin defining a new state on the parent {@link StateMachineBuilder}
         * without needing to call {@link #builder()} or keep a reference to it.
         * @param name The name of the new state
         */
        public StateBuilder state(String name) {
            return StateMachineBuilder.this.state(name);
        }

        /**
         * Convenience method to build the {@link StateMachine} on the parent
         * {@link StateMachineBuilder} without needing to call {@link #builder()} or keep
         * a reference to it.
         * @return The new StateMachine
         */
        public StateMachine<T, R> build() {
            return StateMachineBuilder.this.build();
        }
    }

}