package grails.plugin.externalconfig

import org.grails.io.support.DefaultResourceLoader
import org.grails.io.support.Resource
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource

trait ExternalConfig implements EnvironmentAware {
    private DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader()
    /**
     * Set the {@code Environment} that this object runs in.
     */
    @Override
    void setEnvironment(Environment environment) {
        List locations = environment.getProperty('grails.config.locations', ArrayList, [])
        String encoding = environment.getProperty('grails.config.encoding', String, 'UTF-8')

        for (location in locations) {
            ConfigObject configObject = null
            if (location instanceof Class) {
                println "Loading config class $location"
                configObject = new ConfigSlurper(grails.util.Environment.current.name).parse((Class) location)
            }
            if (location instanceof String) {
                String finalLocation = location as String
                // Replace ~ with value from system property 'user.home' if set
                if(environment.properties.systemProperties.'user.home' && finalLocation.startsWith('~/')) {
                    finalLocation = environment.properties.systemProperties.'user.home' + finalLocation[1..-1]
                }
                Resource resource = defaultResourceLoader.getResource(finalLocation)
                if(resource.exists()) {
                    println "Loading config file $finalLocation"
                    String configText = resource.inputStream.getText(encoding)
                    if (configText) {
                        configObject = new ConfigSlurper(grails.util.Environment.current.name).parse(configText)
                    }
                } else {
                    println "Config file $finalLocation not found"
                }
            }
            if (configObject) {
                ((AbstractEnvironment) environment).propertySources.addFirst(new MapPropertySource(configObject.toString(), configObject.flatten()))
            }
        }

    }

}
