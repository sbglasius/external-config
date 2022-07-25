package test.app

import grails.core.GrailsApplication
import org.springframework.beans.factory.annotation.Value

class TestService {

    GrailsApplication grailsApplication

    @Value('${test.config.value:not read}')
    String configTest

    String getConfigValue() {
        configTest
    }

    String getMapConfigValue() {
        Map map = grailsApplication.config.some.map.cfg
        
        return map['abc'].text
    }
}
