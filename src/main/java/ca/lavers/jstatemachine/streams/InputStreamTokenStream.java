package ca.lavers.jstatemachine.streams;

import ca.lavers.jstatemachine.TokenStream;

import java.io.*;
import java.util.Optional;

public class InputStreamTokenStream implements TokenStream<Character> {

    private final PushbackReader input;

    public InputStreamTokenStream(Reader in) {
        this.input = new PushbackReader(in);
    }

    public InputStreamTokenStream(InputStream is) {
        this(new InputStreamReader(is));
    }

    @Override
    public boolean hasNext() {
        try {
            int c = input.read();
            input.unread(c);
            return c >= 0;
        } catch (IOException e) {
            // TODO -- not ideal :)
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Character> next() {
        try {
            int c = input.read();
            if (c >= 0) {
                return Optional.of((char) c);
            }
            else {
                return Optional.empty();
            }
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the underlying Reader or InputStream.
     */
    public void close() throws IOException {
        input.close();
    }
}
