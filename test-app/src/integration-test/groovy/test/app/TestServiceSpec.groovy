package test.app

import grails.testing.mixin.integration.Integration
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@Integration
@RestoreSystemProperties
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestServiceSpec extends Specification{

    TestService testService

    void setupSpec() {
        System.setProperty('grails.config.locations', '')
    }

    void "Loads from application.yml"() {
        expect:
        testService.configValue == "From application.yml"
    }
}
