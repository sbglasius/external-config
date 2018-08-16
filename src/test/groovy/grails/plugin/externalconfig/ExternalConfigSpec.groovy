package grails.plugin.externalconfig

import grails.web.servlet.context.support.GrailsEnvironment
import org.grails.config.NavigableMap
import org.grails.config.NavigableMapPropertySource
import org.grails.testing.GrailsUnitTest
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import spock.lang.Specification
import spock.lang.Unroll

class ExternalConfigSpec extends Specification implements GrailsUnitTest {

    ConfigurableEnvironment environment = new GrailsEnvironment(grailsApplication)
    ExternalConfigRunListener listener = new ExternalConfigRunListener(null, null)

    def "when getting config without grails.config.location set, the config does not change"() {
        when:
        listener.environmentPrepared(environment)

        then:
        environment.properties == old(environment.properties)
    }

    def "when getting config with configs does not exist, noting changes"() {
        given:
        addToEnvironment('grails.config.locations': ['boguslocation', '/otherboguslocation', 'classpath:bogusclasspath', '~/bogus', 'file://bogusfile', 'http://bogus.server'])

        when:
        listener.environmentPrepared(environment)

        then:
        environment.properties == old(environment.properties)
    }

    def "getting configuration from environment specific location"() {
        given:
        addToEnvironment('environments.test.grails.config.locations':["classpath:/externalConfig.yml"])

        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty("yml.config") == 'yml-expected-value'

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

    def "when getting config with config class that has a canonical config, expect the config to be loaded"() {
        given:
        addToEnvironment(
                'global.config': 'global',
                'grails.config.locations': [ConfigWithCanonicalParameter])


        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty('test.external.config') == 'global-value'
    }

    def "when getting config with config class that has a two levels of canonical config, expect the config to be loaded"() {
        given:
        addToEnvironment(
                'global.config': 'global',
                'grails.config.locations': [ConfigWithCanonicalParameter, ConfigWithSecondLevelCanonicalParameter])


        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty('test.external.config') == 'global-value'
        getConfigProperty('second.external.config') == 'value-of-global-value'
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

    def "when getting config with file in system property user.home"() {
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
        addToEnvironment('grails.config.locations': ['file:${user.home}/.grails/external-config-temp-config.groovy'])

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
        def file = File.createTempFile("other-external-config-temp-config", '.groovy')
        file.text = """\
            config.value = 'expected-value'
            nested { config { value = 'nested-value' } }
            """.stripIndent()

        and:
        addToEnvironment('grails.config.locations': ["file:${file.absolutePath}"])

        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty('config.value') == 'expected-value'
        getConfigProperty('nested.config.value') == 'nested-value'

        cleanup:
        file.delete()

    }

    @Unroll("when getting #configExtension config with file in classpath")
    def "when getting config with file in classpath"() {
        given:
        addToEnvironment('grails.config.locations': ["classpath:/externalConfig.${configExtension}"])

        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty("${configExtension}.config") == expectedValue

        where:
        configExtension | expectedValue
        'yml'           | 'yml-expected-value'
        'properties'    | 'properties-expected-value'
        'groovy'        | 'groovy-expected-value'
    }

    @Unroll("when getting referenced #configExtension config with file in classpath")
    def "when getting referenced config with file in classpath"() {
        given:
        addToEnvironment(
                'global.config': 'global-value',
                'grails.config.locations': ["classpath:/externalConfigWithReferencedValue.${configExtension}"])

        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty("external.config") == expectedValue
        getConfigProperty("external.javaHome") == 'test-'+System.getenv('JAVA_HOME')
        where:
        configExtension | expectedValue
        'yml'           | 'yml-global-value'
        'properties'    | 'properties-global-value'
        'groovy'        | 'groovy-global-value'
    }


    def "when getting yml config with file in classpath and with environments block "() {
        given:
        addToEnvironment('grails.config.locations': ['classpath:/externalConfigEnvironments.yml'])

        when:
        listener.environmentPrepared(environment)

        then:
        getConfigProperty('yml.config') == 'expected-value-test'
    }


    private void addToEnvironment(Map properties = [:]) {
        NavigableMap navigableMap = new NavigableMap()
        navigableMap.merge(properties, true)

        environment.propertySources.addFirst(new NavigableMapPropertySource("Basic config", navigableMap))
    }

    private String getConfigProperty(String key) {
        environment.getProperty(key)
    }
}
