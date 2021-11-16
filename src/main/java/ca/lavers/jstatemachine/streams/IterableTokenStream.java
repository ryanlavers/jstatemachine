package ca.lavers.jstatemachine.streams;

import ca.lavers.jstatemachine.TokenStream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class IterableTokenStream<T> implements TokenStream<T> {
    private final Iterator<T> iterator;

    public IterableTokenStream(Iterable<T> iterable) {
        this(iterable.iterator());
    }

    public IterableTokenStream(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Optional<T> next() {
        try {
            return Optional.ofNullable(iterator.next());
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }
}
