package jp.co.cyberagent.aeromock.dsl.routing

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.co.cyberagent.aeromock.dsl.exception.AeromockBadGrammarException
import jp.co.cyberagent.aeromock.dsl.exception.AeromockPatternSyntaxException;

/**
 * Implementation of {@code condition} directive.
 * @author stormcat24
 */
class ConditionDsl {

    /** 1st argument：Target string */
    String target

    /** 2nd argument：Regular expression for target string */
    Pattern regex

    /** 3rd argument： Closure */
    Closure closure

    /** Request URL */
    String requestUri

    /**
     * Constructor
     * @param args arguments
     * @param requestUri Request URI
     */
    ConditionDsl(args, String requestUri) {
        if (args.length != 3) {
            throw new AeromockBadGrammarException("'condition' must have three arguments.")
        }

        if (args[0] == null || !(args[0] instanceof CharSequence)) {
            throw new AeromockBadGrammarException("'condition' first argument must be not null, and String.")
        }

        if (args[1] == null || !(args[1] instanceof CharSequence)) {
            throw new AeromockBadGrammarException("'condition' second argument must be not null, and String.")
        }

        if (args[2] == null || !(args[2] instanceof Closure)) {
            throw new AeromockBadGrammarException("'condition' third argument must be not null, and Closure.")
        }

        this.target = args[0]
        try {
            this.regex = Pattern.compile(args[1])
        } catch (PatternSyntaxException e) {
            throw new AeromockPatternSyntaxException("'${args[1]}' bad regular expression. ", e)
        }
        this.closure = args[2]
        this.requestUri = requestUri
    }

    /**
     * Process when {@code condition} called.
     * @return closure return value
     */
    def execute() {
        Matcher matcher = regex.matcher(target)

        if (matcher.find()) {
            def values = new LinkedHashMap<String, String>()
            if (matcher.hasGroup()) {
                for (index in 1 .. matcher.groupCount()) {
                    values.put("_" + index, matcher.group(index))
                }
            }

            closure.setDelegate(this)
            closure.setResolveStrategy(Closure.DELEGATE_FIRST)
            return closure.call(values)
        } else {
            return requestUri
        }
    }

    /**
     * Process when method has called in @{code condition}.
     */
    def methodMissing(String methodName, args) {

        if (methodName == "rewrite") {
            requestUri = new RewriteDsl(args).execute(requestUri)
            return requestUri
        }
    }

}
