package test.app

import org.springframework.beans.factory.annotation.Value

class TestController {
    @Value('${test.config.value:not read}')
    String configTest
    def index() {
        render "Value is: $configTest"
    }
}
