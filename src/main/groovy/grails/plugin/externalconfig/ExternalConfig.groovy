package grails.plugin.externalconfig

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

trait ExternalConfig implements EnvironmentAware {
    private ResourceLoader defaultResourceLoader = new DefaultResourceLoader()
    /**
     * Set the {@code Environment} that this object runs in.
     */
    @Override
    void setEnvironment(Environment environment) {
        List locations = environment.getProperty('grails.config.locations', ArrayList, [])
        String encoding = environment.getProperty('grails.config.encoding', String, 'UTF-8')

        for (location in locations) {
            Map properties = null
            if (location instanceof Class) {
                properties = loadClassConfig(location as Class)
            } else {
                String finalLocation = location.toString()
                // Replace ~ with value from system property 'user.home' if set
                if(environment.properties.systemProperties.'user.home' && finalLocation.startsWith('~/')) {
                    finalLocation = "file:${environment.properties.systemProperties.'user.home'}${finalLocation[1..-1]}"
                }
                Resource resource = defaultResourceLoader.getResource(finalLocation)
                if(resource.exists()) {
                    println "resource exists: $resource.filename"
                    // TODO differ between .groovy and .yml files
                    if(finalLocation.endsWith('.groovy')) {
                        properties = loadGroovyConfig(resource, encoding)
                    } else if(finalLocation.endsWith('.yml')) {
                        properties = loadYamlConfig(resource)
                    }
                } else {
                    println "Config file $finalLocation not found"
                }
            }
            if (properties) {
                ((AbstractEnvironment) environment).propertySources.addFirst(new MapPropertySource(location.toString(), properties))
            }
        }

    }


    private Map loadClassConfig(Class location) {
        println "Loading config class ${location.name}"
        new ConfigSlurper(grails.util.Environment.current.name).parse((Class) location)?.flatten()
    }

    private Map loadGroovyConfig(Resource resource, String encoding) {
        println "Loading groovy config file ${resource.URI}"
        String configText = resource.inputStream.getText(encoding)
        return configText ? new ConfigSlurper(grails.util.Environment.current.name).parse(configText)?.flatten() : null
    }

    private Map loadYamlConfig(Resource resource) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean()
        yamlPropertiesFactoryBean.setResources(resource)
        yamlPropertiesFactoryBean.afterPropertiesSet()
        yamlPropertiesFactoryBean.getObject()
    }
}
