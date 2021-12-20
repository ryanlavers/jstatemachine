package ca.lavers.jstatemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks data and state related to a specific stream processing operation by
 * a {@link StateMachine}. {@link Action}s and {@link Matcher}s will be passed
 * a Context object when they are called.
 *
 * @param <T> The type of tokens in the input stream
 * @param <R> The type of tokens to be returned in the output stream
 */
public class Context<T, R> {

    private String currentState;
    private T currentItem;
    private int currentPosition = -1;

    private List<R> outputBuffer = new ArrayList<>();

    private final Map<String, Object> attributes = new HashMap<>();

    private StateMachineException thrownException = null;

    /**
     * Creates a new Context with the specified initial state
     */
    Context(String initialState) {
        currentState = initialState;
    }

    /**
     * Returns the name of the current state the StateMachine is in.
     */
    public String currentState() {
        return currentState;
    }

    /**
     * Change the current state to the given one, by name.
     */
    public void setCurrentState(String name) {
        currentState = name;
    }

    /**
     * Returns the item currently being examined by the StateMachine
     */
    public T currentItem() {
        return currentItem;
    }

    /**
     * Replace the current item with a new one. Be careful; this is likely to
     * cause weird side-effects :)
     * @param item The new item to replace the current one
     */
    public void setCurrentItem(T item) {
        currentItem = item;
    }

    /**
     * Increment the current position counter
     */
    void incrementPosition() {
        this.currentPosition++;
    }

    /**
     * Resets the current position counter
     */
    void clearPosition() {
        this.currentPosition = -1;
    }

    /**
     * Returns the position of the current item in the input stream
     */
    public int currentPosition() {
        return this.currentPosition;
    }

    /**
     * Sends the given item to the output stream
     */
    public void emit(R item) {
        outputBuffer.add(item);
    }

    /**
     * Returns the contents of the internal output buffer and clears it
     */
    List<R> getOutputBufferAndClear() {
        List<R> r = outputBuffer;
        this.outputBuffer = new ArrayList<>();
        return r;
    }

    /**
     * Saves a value as a context attribute. Attributes can be used by
     * custom actions to save any required state or data.
     *
     * @param key The name of the attribute to set
     * @param value The value to store under the specified key
     */
    public void put(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Retrieves a context attribute by name. If there is no attribute by that
     * name, null will be returned. For generics purposes, the class of the
     * expected attribute value must be provided; if the actual value is not of
     * this type, null will be returned. Use {@link #get(String)} if you need
     * to check for the existence of an attribute or to get a value regardless
     * of its type.
     *
     * @param key The name of the attribute to read
     * @param clz The type of value expected to be found under the given key
     * @return The retrieved value, or null if the attribute was not found or was
     *         of a different type than specified
     */
    public <A> A get(String key, Class<A> clz) {
        Object value = attributes.get(key);
        if(clz.isInstance(value)) {
            return clz.cast(value);
        }
        else {
            return null;
        }
    }

    /**
     * Retrieves a context attribute by name. If there is no attribute by that
     * name, null will be returned.
     *
     * @param key The name of the attribute to read
     * @return The retrieved value, or null if the attribute was not found
     */
    public Object get(String key) {
        return attributes.get(key);
    }

    /**
     * Removes a context attribute by name, if it exists.
     *
     * @param key The name of the attribute to remove
     * @return The previous value of the removed attribute; or null if one did not exist
     */
    public Object remove(String key) {
        return attributes.remove(key);
    }

    /**
     * Called internally to indicate that processing has failed with the given error
     */
    void setError(StateMachineException e) {
        this.thrownException = e;
    }

    /**
     * Returns true if processing has failed with an error; the StateMachine will
     * avoid any further input reading and throw the relevant exception instead.
     */
    public boolean isFailed() {
        return this.thrownException != null;
    }

    /**
     * Returns the exception, if any, that caused the failure of execution.
     */
    public StateMachineException getThrownException() {
        return thrownException;
    }
}
