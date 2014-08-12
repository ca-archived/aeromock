package ameba.aeromock.dsl.routing

import spock.lang.Specification
import spock.lang.Unroll
import ameba.aeromock.dsl.exception.AeromockBadGrammarException
import ameba.aeromock.dsl.exception.AeromockPatternSyntaxException;

@Unroll
class ConditionDslSpec extends Specification {

    def "not three paramerters" () {
        
        when:
        new ConditionDsl(args.toArray(), requestUri)
        
        then:
        thrown AeromockBadGrammarException
        
        where:
        requestUri  | args                                                         | _
        "/hoge"     | []                                                           | _
        "/hoge"     | ['param=value', /^param=(.+)$/ ]                             | _
        "/hoge"     | ['param=value', /^param=(.+)$/, {_ -> "/hoge"}, "illegal" ]  | _
    }
    
    
    def "three paramerters, but bad parameter" () {
        
        when:
        new ConditionDsl([target, regex, closure].toArray(), requestUri)
        
        
        then:
        thrown expect
        
        where:
        requestUri   | target        | regex            | closure       | expect
        "/hoge"      | null          | null             | null          | AeromockBadGrammarException
        "/hoge"      | 1111          | null             | null          | AeromockBadGrammarException
        "/hoge"      | 'param=value' | null             | null          | AeromockBadGrammarException
        "/hoge"      | 'param=value' | 1111             | null          | AeromockBadGrammarException
        "/hoge"      | 'param=value' | /^param=(.+)$/   | null          | AeromockBadGrammarException
        "/hoge"      | 'param=value' | /^param=(.+)$/   | "hoge"        | AeromockBadGrammarException
        "/hoge"      | 'param=value' | /^param=(.+)$/   | 1111          | AeromockBadGrammarException
        "/hoge"      | 'param=value' | "*\\.*"          | {_ -> "hoge"} | AeromockPatternSyntaxException
    }
    
    def "execute" () {
        
        when:
        ConditionDsl dsl = new ConditionDsl([target, regex, closure].toArray(), requestUri)
        String actual = dsl.execute()
        
        then:
        actual == expect
        
        where:
        requestUri   | target                        | regex                           | closure                        | expect 
        "/hoge"      | 'key=value'                   | /^param=.+$/                    | {_ -> "/fuga"}                 | "/hoge"
        "/hoge"      | 'param=value'                 | /^param=.+$/                    | {_ -> "/fuga"}                 | "/fuga"
        "/hoge"      | 'param=value'                 | /^param=(.+)$/                  | {_ -> "/fuga/${_._1}"}         | "/fuga/value"
        "/hoge"      | 'param1=value1&param2=value2' | /^param1=(.+)&param2=(.+)$/     | {_ -> "/fuga/${_._1}/${_._2}"} | "/fuga/value1/value2"
        
    }
}
