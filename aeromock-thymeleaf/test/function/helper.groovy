class HelperUtilityObject {

    long round(double number) {
        return Math.round(number)
    }

    String hello(String message) {
        return "Hello!! $message"
    }

}
return new HelperUtilityObject()
