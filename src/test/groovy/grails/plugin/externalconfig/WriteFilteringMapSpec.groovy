package grails.plugin.externalconfig

import spock.lang.Specification

class WriteFilteringMapSpec extends Specification {
    def "get written values with seeded map"() {
        given:
            def original = [a: [b: 'b-value']]
            def filter = new WriteFilteringMap(original)
        when:
            filter.a.c = 'c-value'
        then:
            filter.getWrittenValues().size() == 1
            filter.getWrittenValues().get('a.c') == 'c-value'
    }

    def "get written values without seeding"() {
        given:
            def filter = new WriteFilteringMap()
        when:
            filter.a.c = 'c-value'
        then:
            filter.getWrittenValues().size() == 1
            filter.getWrittenValues().get('a.c') == 'c-value'
    }

    def "get written values from changing existing value"() {
        given:
            def original = [a: [b: 'b-value']]
            def filter = new WriteFilteringMap(original)
        when:
            filter.a.b = 'new-b-value'
        then:
            filter.getWrittenValues().size() == 1
            filter.getWrittenValues().get('a.b') == 'new-b-value'
    }

    def "get written values from deep nesting"() {
        given: "A seeded map"
            def original = [a: [b: 'b-value', c: [d: 'd-value']]]
            def filter = new WriteFilteringMap(original)
        expect: "that the filter map already contains these values"
            filter.a.b == 'b-value'
            filter.a.c.d == 'd-value'
        when: "setting new and existing values"
            filter.a.b = 'new-b-value'
            filter.a.c.e = 'new-e-value'
            filter.a.c.f.g.h = 'new-h-value'
        then: "these values are in the writen values"
            filter.getWrittenValues().size() == 3
            filter.getWrittenValues().get('a.b') == 'new-b-value'
            filter.getWrittenValues().get('a.c.e') == 'new-e-value'
            filter.getWrittenValues().get('a.c.f.g.h') == 'new-h-value'
    }



}
