package grails.plugin.externalconfig

import groovy.transform.CompileStatic
import org.grails.config.NavigableMapPropertySource
import org.grails.config.yaml.YamlPropertySourceLoader
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

@CompileStatic
trait ExternalConfig implements EnvironmentAware {
    private ResourceLoader defaultResourceLoader = new DefaultResourceLoader()
    private YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader()

    /**
     * Set the {@code Environment} that this object runs in.
     */
    @Override
    void setEnvironment(Environment environment) {
        List locations = environment.getProperty('grails.config.locations', ArrayList, [])
        String encoding = environment.getProperty('grails.config.encoding', String, 'UTF-8')

        for (location in locations) {
            MapPropertySource propertySource = null
            if (location instanceof Class) {
                propertySource = loadClassConfig(location as Class)
            } else {
                String finalLocation = location.toString()
                // Replace ~ with value from system property 'user.home' if set
                String userHome = System.properties.getProperty('user.home')
                if(userHome && finalLocation.startsWith('~/')) {
                    finalLocation = "file:${userHome}${finalLocation[1..-1]}"
                }
                Resource resource = defaultResourceLoader.getResource(finalLocation)
                if(resource.exists()) {
                    println "resource exists: $resource.filename"

                    if(finalLocation.endsWith('.groovy')) {
                        propertySource = loadGroovyConfig(resource, encoding)
                    } else if(finalLocation.endsWith('.yml')) {
                        environment.activeProfiles
                        propertySource = loadYamlConfig(resource)

                    } else {
                        // Attempt to load the config as plain old properties file (POPF)
                        propertySource = loadPropertiesConfig(resource)
                    }
                } else {
                    println "Config file $finalLocation not found"
                }
            }
            if (propertySource?.getSource() && !propertySource.getSource().isEmpty()) {
                ((AbstractEnvironment) environment).propertySources.addFirst(propertySource)
            }
        }
    }

    private MapPropertySource loadClassConfig(Class location) {
        println "Loading config class ${location.name}"
        Map properties = new ConfigSlurper(grails.util.Environment.current.name).parse((Class) location)?.flatten()
        new MapPropertySource(location.toString(), properties)
    }

    private MapPropertySource loadGroovyConfig(Resource resource, String encoding) {
        println "Loading groovy config file ${resource.URI}"
        String configText = resource.inputStream.getText(encoding)
        Map properties = configText ? new ConfigSlurper(grails.util.Environment.current.name).parse(configText)?.flatten() : [:]
        new MapPropertySource(resource.filename, properties)
    }

    private NavigableMapPropertySource loadYamlConfig(Resource resource) {
        NavigableMapPropertySource propertySource = yamlPropertySourceLoader.load(resource.filename, resource, null) as NavigableMapPropertySource
        return propertySource
    }

    private MapPropertySource loadPropertiesConfig(Resource resource) {
        Properties properties = new Properties()
        properties.load(resource.inputStream)
        new MapPropertySource(resource.filename, properties as Map)
    }
}
