package grails.plugin.externalconfig

import grails.web.servlet.context.support.GrailsEnvironment
import org.grails.config.NavigableMap
import org.grails.config.NavigableMapPropertySource
import org.grails.testing.GrailsUnitTest
import org.springframework.core.env.ConfigurableEnvironment
import spock.lang.Specification
import spock.lang.Unroll

class MergedConfigSpec extends Specification implements GrailsUnitTest {

    ConfigurableEnvironment environment = new GrailsEnvironment(grailsApplication)
    ExternalConfigRunListener listener = new ExternalConfigRunListener(null, null)

    def cleanupSpec() {
        cleanupGrailsApplication()
        System.clearProperty('micronaut.config.files')
    }


    def "when merging multiple configs the expected values are in the final result"() {
        given:
            addToEnvironment('grails.config.locations': [
                    'classpath:/mergeExternalConfig.yml',
                    'classpath:/mergeExternalConfig.groovy',
                    'classpath:/mergeExternalConfig.properties'
            ])
        when:
            listener.environmentPrepared(environment)

        then:
            getConfigProperty('base.config.yml') == 'yml-expected-value'
            getConfigProperty('base.config.groovy') == 'groovy-expected-value'
            getConfigProperty('base.config.properties') == 'properties-expected-value'
    }

    def "when merging multiple groovy configs the expected values are in the final result"() {
        given:
            addToEnvironment('base.config.global':'global-expected-value',
                    'grails.config.locations': [
                    'classpath:/mergeExternalConfig.groovy',
                    'classpath:/mergeExternalConfig2.groovy',
            ])
        when:
            listener.environmentPrepared(environment)

        then:
            getConfigProperty('base.config.global') == 'global-expected-value'
            getConfigProperty('base.config.groovy') == 'groovy-expected-value'
            getConfigProperty('base.config.groovy2A') == 'groovy2-expected-value-A'
            getConfigProperty('base.config.groovy2') == 'groovy2-expected-value'
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
