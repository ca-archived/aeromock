package ameba.aeromock.dsl.routing

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ameba.aeromock.dsl.exception.AeromockBadGrammarException
import ameba.aeromock.dsl.exception.AeromockPatternSyntaxException;

/**
 * Implementation of {@code rewrite} directive.
 * @author stormcat24
 */
class RewriteDsl {

    Logger LOG = LoggerFactory.getLogger(RewriteDsl.class)

    Pattern uriPattern = Pattern.compile(/(.+)\?.*/)

    /** 1st argument：Regular expression for target string */
    Pattern regex
    /** 2nd argument(1)：When matched regular expression, rewrited URI */
    String to
    /** 2nd argument(2)：When matched regular expression, closure to return rewrited URI. */
    Closure closure

    /**
     * Constructor
     * @param args arguments
     */
    RewriteDsl(args) {
        if (args.length != 2) {
            throw new AeromockBadGrammarException("'rewrite' must have two arguments.")
        }

        if (args[0] == null || (!args[0] instanceof CharSequence)) {
            throw new AeromockBadGrammarException("'rewrite' first argument must be not null, and String.")
        }

        if (args[1] == null) {
            throw new AeromockBadGrammarException("'rewrite' second argument must be not null, and String or Closure.")
        }

        try {
            this.regex = Pattern.compile(args[0])
        } catch (PatternSyntaxException e) {
            throw new AeromockPatternSyntaxException("'${args[0]}' bad regular expression. ", e)
        }

        if (args[1] instanceof CharSequence) {
            this.to = args[1]
        } else if (args[1] instanceof Closure) {
            this.closure = args[1]
        } else {
            throw new AeromockBadGrammarException("'rewrite' second argument must be String or Closure.")
        }

    }

    /**
     * Process when {@code rewrite} has called. Return rewrited URI.
     * @param requestUri Request URI
     * @return Result of URI rewriting
     */
    def execute(String requestUri) {

        String filteredUri = filterUri(requestUri)

        Matcher matcher = regex.matcher(filteredUri)
        if (matcher.find()) {

            String converted = to

            // Closureを優先する
            if (closure != null) {

                def values = new LinkedHashMap<String, String>()
                if (matcher.hasGroup()) {
                    for (index in 1 .. matcher.groupCount()) {
                        values.put("_" + index, matcher.group(index))
                    }
                }

                closure.setDelegate(this)
                closure.setResolveStrategy(Closure.DELEGATE_FIRST)

                converted = closure.call(values)

            } else {
                List<String> merged = new ArrayList<String>()


                if (matcher.hasGroup()) {
                    for (index in 1 .. matcher.groupCount()) {
                        merged.add(matcher.group(index))
                    }
                }

                merged.eachWithIndex { value, index ->
                    converted = converted.replace('$' + (index + 1), value)
                }

            }

            LOG.info("(Rewrite)Rule:{} -> {}, Result:{} -> {}", regex.toString(), to, filteredUri, converted)

            return converted
        } else {
            return requestUri
        }
    }

    private def String filterUri(String requestUri) {
        Matcher uriMatcher = uriPattern.matcher(requestUri)
        if (uriMatcher.find()) {
            return uriMatcher.group(1)
        } else {
            return requestUri
        }

    }
}
