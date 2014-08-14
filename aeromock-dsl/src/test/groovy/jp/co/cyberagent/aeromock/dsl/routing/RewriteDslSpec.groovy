package jp.co.cyberagent.aeromock.dsl.routing

import jp.co.cyberagent.aeromock.dsl.exception.AeromockBadGrammarException
import jp.co.cyberagent.aeromock.dsl.exception.AeromockPatternSyntaxException
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RewriteDslSpec extends Specification {

    def "not two paramerters" () {

        when:
        new RewriteDsl(args.toArray())

        then:
        thrown AeromockBadGrammarException

        where:
        args                           | _
        []                             | _
        ['/hoge']                      | _
        ['/hoge', /^\/hoge$/, 'dummy'] | _

    }

    def "two paramerters, but bad parameter" () {

        when:
        RewriteDsl dsl = new RewriteDsl([from, to].toArray())
        String actual = dsl.execute(requestUri)

        then:
        thrown expect

        where:
        requestUri                    | from                           | to    | expect
        '/hoge'                       | null                           | null  | AeromockBadGrammarException
        '/hoge'                       | 1111                           | null  | AeromockBadGrammarException
        '/hoge'                       | /^\/hoge$/                     | null  | AeromockBadGrammarException
        '/hoge'                       | /^\/hoge$/                     | 1111  | AeromockBadGrammarException
        '/hoge'                       | "*\\.*"                        | 1111  | AeromockPatternSyntaxException
    }

    def "using placeholder" () {

        when:
        RewriteDsl dsl = new RewriteDsl([from, to].toArray())
        String actual = dsl.execute(requestUri)

        then:
        actual == converted

        where:
        requestUri                  | from                                            | to                    | converted
        '/hoge'                     | /^\/hoge$/                                      | '/hoge/hoge'          | '/hoge/hoge'
        '/hoge/fuga'                | /^\/hoge\/((?!hoge).+)$/                        | '/hoge/hoge-$1'       | '/hoge/hoge-fuga'
        '/hoge/neko/fuga-nuko'      | /^\/hoge\/([0-9a-zA-Z]+)\/fuga-([0-9a-zA-Z]+)$/ | '/hoge/$1/$2'         | '/hoge/neko/nuko'
        '/hoge/neko/fuga-nuko?'     | /^\/hoge\/([0-9a-zA-Z]+)\/fuga-([0-9a-zA-Z]+)$/ | '/hoge/$1/$2'         | '/hoge/neko/nuko'
        '/hoge/neko/fuga-nuko?a=1'  | /^\/hoge\/([0-9a-zA-Z]+)\/fuga-([0-9a-zA-Z]+)$/ | '/hoge/$1/$2'         | '/hoge/neko/nuko'
    }


    def "using closure" () {

        when:
        RewriteDsl dsl = new RewriteDsl([from, to].toArray())
        String actual = dsl.execute(requestUri)

        then:
        actual == converted

        where:
        requestUri                  | from                                            | to                             | converted
        '/hoge'                     | /^\/hoge$/                                      | {_ -> "/hoge"}                 | '/hoge'
        '/hoge/fuga'                | /^\/hoge\/((?!hoge).+)$/                        | {_ -> "/hoge/hoge-${_._1}"}    | '/hoge/hoge-fuga'
        '/hoge/neko/fuga-nuko'      | /^\/hoge\/([0-9a-zA-Z]+)\/fuga-([0-9a-zA-Z]+)$/ | {_ -> "/hoge/${_._1}/${_._2}"} | '/hoge/neko/nuko'
        '/hoge/neko/fuga-nuko?'     | /^\/hoge\/([0-9a-zA-Z]+)\/fuga-([0-9a-zA-Z]+)$/ | {_ -> "/hoge/${_._1}/${_._2}"} | '/hoge/neko/nuko'
        '/hoge/neko/fuga-nuko?a=1'  | /^\/hoge\/([0-9a-zA-Z]+)\/fuga-([0-9a-zA-Z]+)$/ | {_ -> "/hoge/${_._1}/${_._2}"} | '/hoge/neko/nuko'
    }
}
