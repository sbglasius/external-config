package test.app

import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import javax.inject.Inject

@Integration
class TestAppIntegrationSpec extends Specification {
    @Autowired
    TestService testService

    @Autowired
    TestSingleton testSingleton

    void "testService returns the correct read value"() {
        expect:
        testService.configValue == 'From Resource Config'
    }

    void "testSingleton returns the correct read value"() {
        expect:
        testSingleton.configValue == 'From Resource Config'

        and:
        testSingleton.micronautOnlyValue == 'Micronaut Only'
    }
}
