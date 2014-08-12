import de.neuland.jade4j.filter.Filter
configuration.setFilter("testfilter", new TestScriptFilter())

class TestScriptFilter implements Filter {

    @Override
    String convert(String source, Map<String, Object> attributes, Map<String, Object> model) {
        println("nekoneko")
        return source
    }
}