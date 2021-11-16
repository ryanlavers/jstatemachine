package ca.lavers.jstatemachine.streams;

import ca.lavers.jstatemachine.TokenStream;

import java.util.Optional;

public class StringStream implements TokenStream<Character> {

    private final String string;
    private int index = 0;

    public StringStream(String string) {
        this.string = string;
    }

    @Override
    public boolean hasNext() {
        return string.length() > index;
    }

    @Override
    public Optional<Character> next() {
        if(hasNext()) {
            return Optional.of(string.charAt(index++));
        }
        else {
            return Optional.empty();
        }
    }
}
