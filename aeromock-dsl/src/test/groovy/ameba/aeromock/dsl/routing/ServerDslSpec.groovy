package ameba.aeromock.dsl.routing

import spock.lang.Specification
import spock.lang.Unroll
import ameba.aeromock.dsl.exception.AeromockBadGrammarException;
import ameba.aeromock.dsl.exception.AeromockPatternSyntaxException;

@Unroll
class ServerDslSpec extends Specification {
    
    // ClosureのownerがこのSpec自身になるため、REQUEST_URIの値を持たせるために定義
    String REQUEST_URI = null
    String HOST = null

    def "not two paramerters" () {
        
        when:
        new ServerDsl(args.toArray())
        
        then:
        thrown AeromockBadGrammarException
        
        where:
        args                           | _
        []                             | _
        ['localhost']                  | _
        ['localhost', 'hoge', 'fuga']  | _
        
    }
    
    def "two paramerters, but bad parameter" () {
        
        when:
        ServerDsl dsl = new ServerDsl([host, closure].toArray())
        dsl.execute(requestUri)

        then:
        thrown expect

        where:
        host           | closure    | expect
        null           | null       | AeromockBadGrammarException
        "localhost"    | null       | AeromockBadGrammarException
        null           | {"/hoge"}  | AeromockBadGrammarException
        1111           | null       | AeromockBadGrammarException
        "localhost"    | "hoge"     | AeromockBadGrammarException
        "localhost"    | 1111       | AeromockBadGrammarException
    }
    
    def "execute" () {
        
        when:
        closure.owner.REQUEST_URI = requestUri
        closure.owner.HOST = realHost
        
        ServerDsl dsl = new ServerDsl([host, closure].toArray())
        String actual = dsl.execute()
        
        then:
        actual == expect
        
        where:
        requestUri     | realHost     | host           | closure                                                 | expect
        '/hoge'        | "localhost"  | "hoge.local"   | { "/hoge" }                                             | null
        '/hoge'        | "localhost"  | "localhost"    | { "/hoge" }                                             | "/hoge"
        '/hoge'        | "localhost"  | "127.0.0.1"    | { "/hoge" }                                             | "/hoge"
        '/hoge'        | "localhost"  | "localhost"    | { rewrite(/^\/hoge$/, '/rewrite') }                     | "/rewrite"
        '/hoge'        | "localhost"  | "localhost"    | { condition "/hoge", /^\/hoge$/, {_ -> "/condition"} }  | "/condition"

    }
}
