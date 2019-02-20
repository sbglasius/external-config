package test.app

import org.springframework.beans.factory.annotation.Value

class TestService {
    @Value('${test.config.value:not read}')
    String configTest

    String getConfigValue() {
        configTest
    }
}
