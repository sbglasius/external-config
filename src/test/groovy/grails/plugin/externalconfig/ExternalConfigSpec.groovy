package grails.plugin.externalconfig

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.web.servlet.context.support.GrailsEnvironment
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
class ExternalConfigSpec extends Specification {
    static class ClassWithExternalConfig implements ExternalConfig {

    }
    Environment environment = new GrailsEnvironment(grailsApplication)

    def "when getting config without grails.config.location set, the config does not change"() {
        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        environment.properties == old(environment.properties)
    }

    def "when getting config with grails.config.location set, and the configs does not exist, noting changes"() {
        given:
        addToEnvironment('grails.config.locations': ['boguslocation','/otherboguslocation','classpath:bogusclasspath','~/bogus', 'file://bogusfile','http://bogus.server'])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        environment.properties == old(environment.properties)
    }

    def "when getting config with grails.config.location with class config, expect the config to be loaded"() {
        given:
        addToEnvironment('grails.config.locations': [grails.plugin.externalconfig.ConfigWithoutEnvironmentBlock])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        getConfigProperty('test.external.config') == 'expected-value'
    }


    private Environment addToEnvironment(Map properties = [:]) {
        ((AbstractEnvironment) environment).propertySources.addFirst(new MapPropertySource("Basic config", properties))
    }

    private String getConfigProperty(String key) {
        ((AbstractEnvironment) environment).getProperty(key)
    }


}
