package grails.plugin.externalconfig

import groovy.transform.CompileStatic
import org.grails.config.NavigableMapPropertySource
import org.grails.config.yaml.YamlPropertySourceLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringApplicationRunListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

@CompileStatic
class ExternalConfigRunListener implements SpringApplicationRunListener {

	private ResourceLoader defaultResourceLoader = new DefaultResourceLoader()
	private YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader()
	private Logger log = LoggerFactory.getLogger('grails.plugin.externalconfig.ExternalConfig')

	public ExternalConfigRunListener(SpringApplication application, String[] args) { }
	
	@Override
	void environmentPrepared(ConfigurableEnvironment environment) {
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
				if (userHome && finalLocation.startsWith('~/')) {
					finalLocation = "file:${userHome}${finalLocation[1..-1]}"
				}
				finalLocation = environment.resolvePlaceholders(finalLocation) 
				
				Resource resource = defaultResourceLoader.getResource(finalLocation)
				if (resource.exists()) {
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
				environment.propertySources.addFirst(propertySource)
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

	void started() { }
	void contextPrepared(ConfigurableApplicationContext context) { }
	void contextLoaded(ConfigurableApplicationContext context) { }
	void finished(ConfigurableApplicationContext context, Throwable exception) { }
}
