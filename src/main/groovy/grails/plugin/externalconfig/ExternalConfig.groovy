package grails.plugin.externalconfig
import groovy.transform.CompileStatic
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment

@CompileStatic
trait ExternalConfig implements EnvironmentAware, ExternallyConfigurable {
    @Override
    void setEnvironment(Environment environment) {
       configureEnvironment(environment)
    }
}
