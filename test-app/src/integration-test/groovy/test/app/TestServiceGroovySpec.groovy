package test.app

import grails.testing.mixin.integration.Integration
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import javax.inject.Inject

@Integration
@RestoreSystemProperties
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestServiceGroovySpec extends Specification {

    TestService testService

    void setupSpec() {
        System.setProperty('grails.config.locations', 'classpath:testResourceConfig.groovy')
    }

    void "Loads from resourceConfig.groovy"() {
        expect:
        testService.configValue == "From Test Resource Config (.groovy)"
    }

    @Integration
    @RestoreSystemProperties
    @DirtiesContext(classMode =  DirtiesContext.ClassMode.BEFORE_CLASS)
    static class TestAppIntegrationSpec extends Specification {
        TestService testService
    
        @Inject
        TestSingleton testSingleton
    
        void setupSpec() {
            System.setProperty('grails.config.locations', 'classpath:resourceConfig.yml')
        }
    
        void "testService returns the correct read value"() {
            expect:
            testService.configValue == 'From Resource Config'
        }
    
        void "testSingleton returns the correct read value"() {
            expect: 'Specific Grails value read'
            testSingleton.configValue == 'From Resource Config'
    
            and: 'Generic micronaut value read'
            testSingleton.micronautOnlyValue == 'Micronaut Only'
        }
    }
}
