package ca.lavers.jstatemachine;

import java.util.*;

/**
 * A set of rules for a single state in a StateMachine
 */
class State<T, R> {

    private final List<Rule<T, R>> rules = new ArrayList<>();
    private List<Action<T, R>> otherwiseActions = new ArrayList<>();
    private List<Action<T, R>> endActions = new ArrayList<>();

    void addRule(Matcher<T, R> matcher, Action<T, R>[] actions) {
        rules.add(new Rule<T, R>(matcher, actions));
    }

    void setOtherwiseActions(Action<T, R>... actions) {
        this.otherwiseActions = Arrays.asList(actions);
    }

    void setEndActions(Action<T, R>... actions) {
        this.endActions = Arrays.asList(actions);
    }

    List<Action<T, R>> actionsFor(Context<T, R> ctx) {
        Optional<Rule<T, R>> rule = rules.stream().filter(r -> r.matches(ctx)).findFirst();
        if(rule.isPresent()) {
            return rule.get().actions;
        }

        return otherwiseActions;
    }

    List<Action<T, R>> getEndActions() {
        return endActions;
    }
}

class Rule<T, R> {
    public Matcher<T, R> matcher;
    public List<Action<T, R>> actions;

    Rule(Matcher<T, R> matcher, Action<T, R>[] actions) {
        this.matcher = matcher;
        this.actions = Arrays.asList(actions);
    }

    boolean matches(Context<T, R> ctx) {
        return matcher.matches(ctx);
    }
}