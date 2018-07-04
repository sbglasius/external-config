package grails.plugin.externalconfig

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplicationRunListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.ConfigurableEnvironment

@CompileStatic
class ExternalConfigRunListener implements SpringApplicationRunListener, ExternallyConfigurable {

	@Override
	void environmentPrepared(ConfigurableEnvironment environment) {
		configureEnvironment(environment)
	}
	// Spring Boot 1.4 or higher
	void starting() {}
	// Spring Boot 1.3
	void started() {}
	void contextPrepared(ConfigurableApplicationContext context) {}
	void contextLoaded(ConfigurableApplicationContext context) { }
	void finished(ConfigurableApplicationContext context, Throwable exception) { }
}
