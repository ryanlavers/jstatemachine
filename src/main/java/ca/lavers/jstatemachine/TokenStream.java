package ca.lavers.jstatemachine;

import ca.lavers.jstatemachine.streams.InputStreamTokenStream;
import ca.lavers.jstatemachine.streams.IterableTokenStream;
import ca.lavers.jstatemachine.streams.StringStream;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A simple stream of objects; a {@link StateMachine} operates on an input
 * stream and returns an output stream, both of this type.
 *
 * @param <T> The type of objects contained in this stream.
 */
public interface TokenStream<T> {
    /**
     *  Returns true if this stream has at least one more item that hasn't
     *  been read.
     */
    boolean hasNext();

    /**
     * Returns the next item from this stream; the Optional will be empty
     * if there are no more items to be read.
     */
    Optional<T> next();

    /**
     * Iterates over this stream, reading each item and passing it to the
     * provided consumer function.
     */
    default void consume(Consumer<T> consumer) {
        while(true) {
            Optional<T> item = next();
            if(item.isPresent()) {
                consumer.accept(item.get());
            }
            else {
                return;
            }
        }
    }

    /**
     * Creates a new stream containing the individual characters of a String
     */
    static TokenStream<Character> of(String string) {
        return new StringStream(string);
    }

    /**
     * Creates a new stream containing the individual characters read from the
     * provided Reader. Note: Reader is NOT closed automatically when the end
     * of the stream is reached. Any IOExceptions thrown will be wrapped in
     * a RuntimeException.
     */
    static TokenStream<Character> of(Reader reader) {
        return new InputStreamTokenStream(reader);
    }

    /**
     * Creates a new stream containing the individual characters read from the
     * provided InputStream. Note: InputStream is NOT closed automatically when
     * the end of the stream is reached. Any IOExceptions thrown will be wrapped in
     * a RuntimeException. Characters will be read using the default charset;
     * pass your own Reader (such as InputStreamReader) to have control over
     * the charset used.
     */
    static TokenStream<Character> of(InputStream is) {
        return new InputStreamTokenStream(is);
    }

    /**
     * Creates a new stream containing the individual items read from the
     * provided Iterable.
     */
    static <T> TokenStream<T> of(Iterable<T> it) {
        return new IterableTokenStream<T>(it);
    }

    /**
     * Creates a new stream containing the individual items read from the
     * provided Iterator.
     */
    static <T> TokenStream<T> of(Iterator<T> it) {
        return new IterableTokenStream<T>(it);
    }

}
