package test.app

import grails.testing.mixin.integration.Integration
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@Integration
@RestoreSystemProperties
@DirtiesContext
class TestServiceGroovySpec extends Specification {

    @Autowired
    TestService testService

    void setupSpec() {
        System.setProperty('grails.config.locations', 'classpath:resourceConfig.groovy')
    }

    void "Loads from resourceConfig.groovy"() {
        expect:
        testService.configValue == "resourceConfig.groovy"
    }
}
