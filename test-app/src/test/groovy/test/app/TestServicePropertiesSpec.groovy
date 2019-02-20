package test.app

import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@Integration
@RestoreSystemProperties
@DirtiesContext
class TestServicePropertiesSpec extends Specification {

    @Autowired
    TestService testService

    void setupSpec() {
        System.setProperty('grails.config.locations', 'classpath:resourceConfig.properties')
    }

    void "Loads from resourceConfig.properties"() {
        expect:
        testService.configValue == "resourceConfig.properties"
    }
}
