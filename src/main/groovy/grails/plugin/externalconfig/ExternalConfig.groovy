package grails.plugin.externalconfig

import javax.naming.Context
import javax.naming.InitialContext
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.config.NavigableMapPropertySource
import org.grails.config.yaml.YamlPropertySourceLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

//@CompileStatic
trait ExternalConfig implements EnvironmentAware {
    private ResourceLoader defaultResourceLoader = new DefaultResourceLoader()
    private YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader()
    private Logger log = LoggerFactory.getLogger('grails.plugin.externalconfig.ExternalConfig')

    /**
     * Returns the name of the prefix to be used for config file property names. If null is returned, the name returned
     * from getExternalConfigKey will be used. e.g. A prefix of appPrefix would return config property names:
     *
     *  appPrefix.config
     *  appPrefix.database.config
     *  appPrefix.logging.config
     *  appPrefix.external.config
     *  appPrefix.vendor.config
     *
     * @return  the name to be used as the prefix for computed property names.
     */
    String getExternalConfigPrefix() {
        return null
    }

    /**
     *  Override to return the key to be used to obtain the prefix in the grails application config.
     *  The default value is "info.app.name" which by default is configured as the application name.
     *
     * @return  The name of the key to be used to obtain the prefix
     */

    String getExternalConfigKey() {
        return "info.app.name"
    }

    /**
     *  Override to return the names of application environment entities to be used in retreiving a path
     *  of application config files.
     *
     * @return  A list of Application Environment Entry Names
     */
    List<String> getExternalConfigEnvironmentNames() {
        return ["CONFIG","EXTERNAL_CONFIG","LOGGING_CONFIG","DATABASE_CONFIG"]
    }

    @Override
    void setEnvironment(Environment environment) {
        List locations = environment.getProperty('grails.config.locations', ArrayList, [])
        String encoding = environment.getProperty('grails.config.encoding', String, 'UTF-8')
        String defaultConfigPrefix =  this.externalConfigPrefix ?:
                environment.getProperty(this.externalConfigKey,String,"app")

        this.externalConfigEnvironmentNames.each {
            try {
                def external_config = ((Context)(new InitialContext().lookup("java:comp/env"))).lookup("${it}")

                if(external_config) {
                    log.info("Loading configuration: $external_config")
                    locations <<  "${external_config}"
                }
            } catch (Exception e) {
                log.debug("External configuration lookup failed: " + e)
            }
        }

        [
                defaultConfigPrefix,"database","logging","external"
        ].each {
            String configKey = it == defaultConfigPrefix ?
                    "${defaultConfigPrefix}.config" :
                    "${defaultConfigPrefix}.${it}.config"
            String configPath = System.properties[configKey]
            if (System.properties[configPath]) {
                locations << "file:" + System.properties[configPath]
            }
        }

        for (location in locations) {
            MapPropertySource propertySource = null
            if (location instanceof Class) {
                propertySource = loadClassConfig(location as Class)
            } else {
                String finalLocation = location.toString()
                // Replace ~ with value from system property 'user.home' if set
                String userHome = System.properties.getProperty('user.home')
                if (userHome && finalLocation.startsWith('~/')) {
                    finalLocation = "file:${userHome}${finalLocation[1..-1]}"
                }
                Resource resource = defaultResourceLoader.getResource(finalLocation)
                if (resource.exists()) {
                    log.debug "resource exists: $resource.filename"

                    if (finalLocation.endsWith('.groovy')) {
                        propertySource = loadGroovyConfig(resource, encoding)
                    } else if (finalLocation.endsWith('.yml')) {
                        environment.activeProfiles
                        propertySource = loadYamlConfig(resource)
                    } else {
                        // Attempt to load the config as plain old properties file (POPF)
                        propertySource = loadPropertiesConfig(resource)
                    }
                } else {
                    log.debug("Config file {} not found", [finalLocation] as Object[])
                }
            }
            if (propertySource?.getSource() && !propertySource.getSource().isEmpty()) {
                ((AbstractEnvironment) environment).propertySources.addFirst(propertySource)
            }
        }
    }

    private MapPropertySource loadClassConfig(Class location) {
        log.info("Loading config class {}", location.name)
        Map properties = new ConfigSlurper(grails.util.Environment.current.name).parse((Class) location)?.flatten()
        new MapPropertySource(location.toString(), properties)
    }

    private MapPropertySource loadGroovyConfig(Resource resource, String encoding) {
        log.info("Loading groovy config file {}", resource.URI)
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
