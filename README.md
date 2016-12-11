External-Config
===============
[![Build Status](https://travis-ci.org/sbglasius/external-config.svg?branch=master)](https://travis-ci.org/sbglasius/external-config)

This plugin will mimic the Grails 2 way of handling external configurations defined in `grails.config.locations`.

Installation
------------

Add dependency to your `build.gradle`:

```
dependencies {
    compile 'org.grails.plugins:external-config:1.0.1'
}
```

To use a snapshot-version

add JFrog OSS Repository to the `repositories`:
```
repositories {
    maven { url "https://repo.grails.org/grails/core" }
}
```

and specify the snapshot version as a dependency:
```
dependencies {
    compile 'org.grails.plugins:external-config:1.0.0-SNAPSHOT'
}
```

Usage
-----

Locate your Grails projects `Application.groovy` and implement the trait `grails.plugin.externalconfig.ExternalConfig`:

```groovy
import grails.plugin.externalconfig.ExternalConfig

class Application extends GrailsAutoConfiguration implements ExternalConfig {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
```

This will add external-config loading to your Grails application.

Static Configuration
------------
Define the property `grails.config.locations` in either `application.yml` like this:

```groovy
grails:
    config:
        locations:
            - classpath:myconfig.groovy
            - classpath:myconfig.yml
            - classpath:myconfig.properties
            - file:///etc/app/myconfig.groovy
            - file:///etc/app/myconfig.yml
            - file:///etc/app/myconfig.properties
            - ~/.grails/myconfig.groovy
            - ~/.grails/myconfig.yml
            - ~/.grails/myconfig.properties
```

or in `application.groovy` like this:

```groovy
grails.config.locations = [
        "classpath:myconfig.groovy",
        "classpath:myconfig.yml",
        "classpath:myconfig.properties",
        "file:///etc/app/myconfig.groovy",
        "file:///etc/app/myconfig.yml",
        "file:///etc/app/myconfig.properties",
        "~/.grails/myconfig.groovy",
        "~/.grails/myconfig.yml",
        "~/.grails/myconfig.properties"
]
```

Notice, that `~/` references the users `$HOME` directory.

The plugin will skip configuration files that are not found. 

For `.groovy` and `.yml` files the `environments` blocks in the config file are interpreted the same way, as in `application.yml` or `application.groovy`.


Runtime Configuration
------------

You may also specify a configuration at runtime using either JNDI application environment entities, 
or using system properties. Either method may be customized via overriding methods which return the 
approiate keys that the web application will look for. See 'customization' section for details.

**System Properties**

In order to use system properties via command line argments, either via IDE or terminal the following additions 
should be added to `build.gradle` file:


```groovy
bootRun {
    systemProperties = System.properties
}

test {
    systemProperties = System.properties
}
```

Afterwhich, you may include external configs using '-D' arugements which match the system properties
the application is seeking. By default the followign should work:

```
-DappName.config="/path/to/config"
-DappName.external.config="/path/to/config"
-DappName.database.config="/path/to/config"
-DappName.logging.config="/path/to/config"

```

**Application Environment Entities**

By default, environment entities with names CONFIG, EXTERNAL_CONFIG, DATABASE_CONFIG, and LOGGING_CONFIG shall be 
considered, and should be set to the full paths external configurations you would like to load.

Tomcat configuration:

```xml

<Context path=""
        docBase="/path/to/app.war" 
        reloadable="false">
        <Environment name="APP_CONFIG"
                value="file:/path/to/external_config.groovy"
                type="java.lang.String"/>
        <Environment name="DATABASE_CONFIG"
                value="file:/path/to/external_config.database.groovy"
                type="java.lang.String"/>
</Context>
```

**Customization**

```groovy
    /**
     * Returns the name of the prefix to be used for config file property names. If null is returned, the name returned
     * from getExternalConfigKey will be used. e.g. A prefix of appPrefix would return config property names:
     *
     *  appPrefix.config
     *  appPrefix.database.config
     *  appPrefix.logging.config
     *  appPrefix.external.config
     *  appPrefix.vendor.config
     *
     * @return  the name to be used as the prefix for computed property names.
     */
    String getExternalConfigPrefix() {
        return null
    }

    /**
     *  Override to return the key to be used to obtain the prefix in the grails application config.
     *  The default value is "info.app.name" which by default is configured as the application name.
     *
     * @return  The name of the key to be used to obtain the prefix
     */

    String getExternalConfigKey() {
        return "info.app.name"
    }

    /**
     *  Override to return the names of application environment entities to be used in retreiving a path
     *  of application config files.
     *
     * @return  A list of Application Environment Entry Names
     */
    List<String> getExternalConfigEnvironmentNames() {
        return ["CONFIG","APP_CONFIG","EXTERNAL_CONFIG","LOGGING_CONFIG","DATABASE_CONFIG"]
    }
```