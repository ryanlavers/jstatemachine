import ca.lavers.jstatemachine.*;

import static ca.lavers.jstatemachine.Actions.error;
import static ca.lavers.jstatemachine.Actions.state;

public class TestMachine {

    // Our output tokens
    static class Token {
        public String type;
        public String value;

        public Token(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public static void main(String[] args) throws StateMachineException {

        // Define a state machine to parse simple embedded color tags from text
        StateMachine<Character, Token> sm = new StateMachineBuilder<Character, Token>()
            .setContextInitializer(ctx -> {
                // Our custom actions expect this attribute to exist
                ctx.put("characterBuffer", new StringBuilder());
            })
            .state("text")
                .on('[', state("opening_bracket"))
                .otherwise(accept())
                .onEnd(emit("text"))
            .state("opening_bracket")
                .on('[', accept(), state("text"))
                .on(']', emit("text"), emit("default_color"), state("text"))
                .onEnd(error("Unclosed color tag"))
                .otherwise(emit("text"), accept(), state("color"))
            .state("color")
                .on(']', emit("color"), state("text"))
                .otherwise(accept())
                .onEnd(error("Unclosed color tag"))
            .build();

        // Some test text to process through the machine
        String text = "Make text [red]red[] like this: [[red]text[[]";
        TokenStream<Token> output = sm.process(TokenStream.of(text));

        // Print the resulting tokens from the output stream
        try {
            output.consume(token -> {
                System.out.println(token.type + ": " + token.value);
            });
        } catch(StateMachineException e) {
            // If an error action is executed, an exception is thrown
            System.out.println(
                    "State machine halted with error: '" + e.getOriginalMessage() +
                    "'  at position " + e.getPosition()
            );
        }
    }

    // Custom action which appends the current character to an internal buffer to
    // be emitted later
    private static Action<Character, Token> accept() {
        return ctx -> {
            ctx.get("characterBuffer", StringBuilder.class).append(ctx.currentItem());
        };
    }

    // Custom action which emits (outputs) the internal buffer as a Token with
    // the given type, and then clears the buffer.
    private static Action<Character, Token> emit(final String type) {
        return ctx -> {
            StringBuilder builder = ctx.get("characterBuffer", StringBuilder.class);
            ctx.emit(new Token(type, builder.toString()));
            builder.setLength(0);
        };
    }
}
