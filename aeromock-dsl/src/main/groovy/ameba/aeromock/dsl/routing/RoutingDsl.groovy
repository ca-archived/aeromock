package ameba.aeromock.dsl.routing

/**
 * Implementation of {@code routing} directive.
 * @author stormcat24
 */
class RoutingDsl {

    def call(Closure closure) {
        closure.setDelegate(this)
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        closure.call()
        return _requestUri
    }

    def _requestUri

    def methodMissing(String methodName, args) {

        if (methodName == "server") {
            ServerDsl server = new ServerDsl(args)
            String result = server.execute()
            if (result != null) {
                _requestUri = result
            }
        }

    }
}
