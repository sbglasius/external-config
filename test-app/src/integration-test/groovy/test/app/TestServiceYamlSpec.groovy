package test.app

import grails.testing.mixin.integration.Integration
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@Integration
@RestoreSystemProperties
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestServiceYamlSpec extends Specification {

    TestService testService

    void setupSpec() {
        System.setProperty('grails.config.locations', 'classpath:testResourceConfig.yml')
    }

    void "Loads from resourceConfig.yml"() {
        expect:
        testService.configValue == "From Test Resource Config (.yml)"
    }
}
