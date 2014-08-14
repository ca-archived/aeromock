package jp.co.cyberagent.aeromock.dsl.routing

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RoutingDslSpec extends Specification {

    def "pattern" () {

        setup:
        GroovyScriptEngine engine = createEngine()

        when:
        Binding binding = new Binding()
        binding.setVariable("routing", new RoutingDsl())
        binding.setVariable("HOST", host)
        binding.setVariable("REQUEST_URI", requestUri)
        binding.setVariable("QUERY_STRING", queryString)

        String actual = engine.run(script, binding)

        then:
        actual == expect

        where:
        script                       | host            | requestUri          | queryString                | expect
        "routing_empty.groovy"       | "localhost"     | "/hoge"             | ""                         | null
        "routing_localhost.groovy"   | "localhost"     | "/hoge"             | ""                         | "/hoge/hoge"
        "routing_localhost.groovy"   | "127.0.0.1"     | "/hoge"             | ""                         | "/hoge/hoge"
        "routing_virtualhost.groovy" | "localhost"     | "/hoge"             | ""                         | null
        "routing_virtualhost.groovy" | "vhost1.local"  | "/hoge"             | ""                         | "/hoge/vhost1"
        "routing_virtualhost.groovy" | "vhost1.local"  | "/hoge"             | "param1=foo1&param2=bar1"  | "/hoge/vhost1/foo1/bar1"
        "routing_virtualhost.groovy" | "vhost2.local"  | "/hoge"             | ""                         | "/hoge/vhost2"
        "routing_virtualhost.groovy" | "vhost2.local"  | "/hoge"             | "param1=foo2&param2=bar2"  | "/hoge/vhost2/foo2/bar2"
        "routing_virtualhost.groovy" | "vhost1.local"  | "/fuga/foo"         | ""                         | "/fuga/vhost1/foo"
        "routing_virtualhost.groovy" | "vhost2.local"  | "/fuga/foo"         | ""                         | "/fuga/vhost2/foo"
        "routing_virtualhost.groovy" | "vhost1.local"  | "/closure/foo/bar"  | ""                         | "/closure/vhost1/hoge-foo/fuga-bar"
        "routing_virtualhost.groovy" | "vhost1.local"  | "/closure/foo/bar"  | "param1=foo1&param2=bar1"  | "/closure/vhost1/hoge-foo/fuga-bar/foo1/bar1"
        "routing_virtualhost.groovy" | "vhost2.local"  | "/closure/foo/bar"  | ""                         | "/closure/vhost2/hoge-foo/fuga-bar"
        "routing_virtualhost.groovy" | "vhost2.local"  | "/closure/foo/bar"  | "param1=foo2&param2=bar2"  | "/closure/vhost2/hoge-foo/fuga-bar/foo2/bar2"
    }


    private def createEngine() {

//        URL url = Thread.currentThread().getContextClassLoader().getResource("ameba/aeromock/dsl/routing/")
//        println("url = $url")
//        return new GroovyScriptEngine(url)

        // ※src/test/resourcesがcompileGroovyタスクでクラスパスにコピーされないので・・・
        // TODO groovyのタスクにコピー処理入れるか検討
        return new GroovyScriptEngine("testscripts/routing/")
    }
}
