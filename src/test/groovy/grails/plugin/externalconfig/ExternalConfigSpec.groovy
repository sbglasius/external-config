package grails.plugin.externalconfig

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.web.servlet.context.support.GrailsEnvironment
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import spock.lang.IgnoreIf
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

    def "when getting config with configs does not exist, noting changes"() {
        given:
        addToEnvironment('grails.config.locations': ['boguslocation','/otherboguslocation','classpath:bogusclasspath','~/bogus', 'file://bogusfile','http://bogus.server'])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        environment.properties == old(environment.properties)
    }

    def "when getting config with config class, expect the config to be loaded"() {
        given:
        addToEnvironment('grails.config.locations': [ConfigWithoutEnvironmentBlock])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        getConfigProperty('test.external.config') == 'expected-value'
    }

    @IgnoreIf({ !(new File(System.getProperty('user.home')).exists()) }) // Fails on travis, but not locally
    def "when getting config with file in user.home"() {
        given:
        def file = new File("/${System.getProperty('user.home')}/.grails", 'external-config-temp-config.groovy')
        file.text = """\
            config.value = 'expected-value'
            nested { config { value = 'nested-value' } }
            """.stripIndent()

        and:
        addToEnvironment('grails.config.locations': ['~/.grails/external-config-temp-config.groovy'])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        getConfigProperty('config.value') == 'expected-value'
        getConfigProperty('nested.config.value') == 'nested-value'

        cleanup:
        file.delete()

    }

    def "when getting config with file in specific folder"() {
        given:
        def file= File.createTempFile("other-external-config-temp-config",'.groovy')
        file.text = """\
            config.value = 'expected-value'
            nested { config { value = 'nested-value' } }
            """.stripIndent()

        and:
        addToEnvironment('grails.config.locations': ["file://${file.absolutePath}"])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        getConfigProperty('config.value') == 'expected-value'
        getConfigProperty('nested.config.value') == 'nested-value'

        cleanup:
        file.delete()

    }

    def "when getting groovy config with file in classpath"() {
        given:
        addToEnvironment('grails.config.locations': ['classpath:/externalConfig.groovy'])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        getConfigProperty('external.config') == 'expected-value'
    }

    def "when getting yml config with file in classpath"() {
        given:
        addToEnvironment('grails.config.locations': ['classpath:/externalConfig.yml'])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        getConfigProperty('yml.config') == 'expected-value'
    }

    def "when getting properties config with file in classpath"() {
        given:
        addToEnvironment('grails.config.locations': ['classpath:/externalConfig.properties'])

        when:
        new ClassWithExternalConfig(environment: environment)

        then:
        getConfigProperty('propertyFile.config') == 'expected-value'
    }

    private Environment addToEnvironment(Map properties = [:]) {
        ((AbstractEnvironment) environment).propertySources.addFirst(new MapPropertySource("Basic config", properties))
    }

    private String getConfigProperty(String key) {
        ((AbstractEnvironment) environment).getProperty(key)
    }
}
