package ameba.aeromock.dsl.exception

import java.util.regex.PatternSyntaxException;

/**
 * Exception when regular expression error.
 * <p>Wrap {@link PatternSyntaxException}.</p>
 * @author stormcat24
 *
 */
class AeromockPatternSyntaxException extends RuntimeException {

    /**
     * Constructor
     * @param message error message
     * @param e {@link PatternSyntaxException}
     */
    AeromockPatternSyntaxException(String message, PatternSyntaxException e) {
        super(message, e)
    }

}
