package test.app

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class TestControllerSpec extends Specification implements ControllerUnitTest<TestController> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
