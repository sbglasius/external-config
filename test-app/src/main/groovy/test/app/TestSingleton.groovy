package test.app

import io.micronaut.context.annotation.Value

import javax.inject.Singleton

@Singleton
class TestSingleton {
    @Value('${test.config.value:not read}')
    String configTest

    @Value('${test.micronaut.only:not read}')
    String micronautOnly

    String getConfigValue() {
        configTest
    }

    String getMicronautOnlyValue() {
        micronautOnly
    }
}
