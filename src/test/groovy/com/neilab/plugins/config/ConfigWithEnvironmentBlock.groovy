package com.neilab.plugins.config

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
