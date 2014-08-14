package jp.co.cyberagent.aeromock.dsl.exception

/**
 * Exception when syntax error of aeromock-dsl
 * @author stormcat24
 */
class AeromockBadGrammarException extends RuntimeException {

    /**
     * Constructor
     * @param message error message
     */
    AeromockBadGrammarException(String message) {
        super(message)
    }

}
