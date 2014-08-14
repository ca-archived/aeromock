package jp.co.cyberagent.aeromock.dsl.routing

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import jp.co.cyberagent.aeromock.dsl.exception.AeromockBadGrammarException

/**
 * Implementation of {@code server} directive.
 * @author stormcat24
 */
class ServerDsl {

    Logger LOG = LoggerFactory.getLogger(ServerDsl.class)

    /** 1st argument： Rewrite target */
    def hostName

    /** 2nd argument： Closure to return rewrited URI. */
    def closure
    def ignoreHosts = ["localhost", "127.0.0.1"]


    /** temporary */
    def _requestUri = null

    /**
     * Constructor
     * @param args arguments
     */
    ServerDsl(args) {
        if (args.length != 2) {
            throw new AeromockBadGrammarException("'server' must have two arguments.")
        }

        if (args[0] == null || !(args[0] instanceof CharSequence)) {
            throw new AeromockBadGrammarException("'server' first argument must be not null, and String.")
        }

        if (args[1] == null || !(args[1] instanceof Closure)) {
            throw new AeromockBadGrammarException("'server' second argument must be not null, and Closure.")
        }

        this.hostName = args[0]
        this.closure = args[1]
    }

    /**
     * Process when {@code server} called.
     * <p>If not matched hostname, return {@code null}.
     * @return result of URI rewriting
     */
    def execute() {
        if (this.closure.owner.HOST.startsWith(hostName) || ignoreHosts.contains(hostName)) {
            closure.setDelegate(this)
            closure.setResolveStrategy(Closure.DELEGATE_FIRST)
            return closure.call()
        } else {
            return null
        }
    }

    /**
     * Process when method has called in @{code server}.
     * @param methodName called method name
     * @param args arguments
     */
    def methodMissing(String methodName, args) {

        if (_requestUri == null) {
            _requestUri = this.closure.owner.REQUEST_URI
        }

        String before = _requestUri

        if (methodName == "rewrite") {
            RewriteDsl rewriteMethod = new RewriteDsl(args)
            String result = rewriteMethod.execute(_requestUri)
            _requestUri = result
        } else if (methodName == "condition") {
            String result = new ConditionDsl(args, _requestUri).execute()
            if (result != null) {
                _requestUri = result
            }
        }

    }

}
