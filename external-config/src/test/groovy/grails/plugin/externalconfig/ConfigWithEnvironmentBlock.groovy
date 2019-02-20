package grails.plugin.externalconfig

test {
    external {
        config = 'expected-value-regular'
        environments {
            development {
                config = 'expected-value-dev'
            }
            test {
                config = 'expected-value-test'
            }
        }
    }
}
