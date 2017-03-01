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

    Environment environment = new GrailsEnvironment(grailsApplication)
    ExternalConfigRunListener listener = new ExternalConfigRunListener(null, null)

    def "when getting config without grails.config.location set, the config does not change"() {
        when:
        listener.environmentPrepared(environment)

        then:
        environment.properties == old(environment.properties)
    }

    def "when getting config with configs does not exist, noting changes"() {
        given:
        addToEnvironment('grails.config.locations': ['boguslocation','/otherboguslocation','classpath:bogusclasspath','~/bogus', 'file://bogusfile','http://bogus.server'])

        when:
        listener.environmentPrepared(environment)

        then:
        environment.properties == old(environment.properties)
    }

    def "when getting config with config class, expect the config to be loaded"() {
        given:
        addToEnvironment('grails.config.locations': [ConfigWithoutEnvironmentBlock])

        when:
       listener.environmentPrepared(environment)

        then:
        getConfigProperty('test.external.config') == 'expected-value'
    }

    def "when getting config with config class and environment block, expect the config to be loaded"() {
        given:
        addToEnvironment('grails.config.locations': [ConfigWithEnvironmentBlock])

        when:
       listener.environmentPrepared(environment)

        then:
        getConfigProperty('test.external.config') == 'expected-value-test'
    }

    def "when getting config with file in user.home"() {
        given: "The home directory of the user"
        def dir = new File("${System.getProperty('user.home')}/.grails")
        dir.mkdirs()

        and: "a new external configuration file"
        def file = new File(dir, 'external-config-temp-config.groovy')
        file.text = """\
            config.value = 'expected-value'
            nested { config { value = 'nested-value' } }
            """.stripIndent()

        and:
        addToEnvironment('grails.config.locations': ['~/.grails/external-config-temp-config.groovy'])

        when:
        listener.environmentPrepared(environment)

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
       listener.environmentPrepared(environment)

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
        listener.environmentPrepared(environment)

        then:
        getConfigProperty('external.config') == 'expected-value'
    }

    def "when getting yml config with file in classpath"() {
        given:
        addToEnvironment('grails.config.locations': ['classpath:/externalConfig.yml'])

        when:
       listener.environmentPrepared(environment)

        then:
        getConfigProperty('yml.config') == 'expected-value'
    }

    def "when getting properties config with file in classpath"() {
        given:
        addToEnvironment('grails.config.locations': ['classpath:/externalConfig.properties'])

        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty('propertyFile.config') == 'expected-value'
    }

    def "when getting yml config with file in classpath and with environments block "() {
        given:
        addToEnvironment('grails.config.locations': ['classpath:/externalConfigEnvironments.yml'])

        when:
       listener.environmentPrepared(environment)

        then:
        getConfigProperty('yml.config') == 'expected-value-test'
    }


    private Environment addToEnvironment(Map properties = [:]) {
        ((AbstractEnvironment) environment).propertySources.addFirst(new MapPropertySource("Basic config", properties))
    }

    private String getConfigProperty(String key) {
        ((AbstractEnvironment) environment).getProperty(key)
    }
}
