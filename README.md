Application-Config
===============
[![Build Status](https://travis-ci.org/neilabdev/application-config.svg?branch=master)](https://travis-ci.org/neilabdev/application-config)

This plugin based on the amazing work started in plugin  [external-config](http://plugins.grails.org/plugin/grails/external-config) by [Sudhir Nimavat](https://github.com/snimavat) and  [Dennie de Lange](https://github.com/tkvw),
 will mimiced the Grails 2 way of handling external configurations defined in `grails.config.locations`; with the necessary additions to allow configurations via command line, system properties, and JNDI.

Contributors
------------

* [neilabdev](https://github.com/neilabdev)


Installation
------------

Add dependency to your `build.gradle`:

```
dependencies {
    compile 'com.neilab.plugins:application-config:1.1.3'
}
```

Usage
-----

When you add this plugin to your Grails build, it will automatically look for the property `grails.config.locations`. Define this in in either `application.yml` like this:

```
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
            - file:${catalina.base}/myconfig.groovy
            - file:${catalina.base}/myconfig.yml
            - file:${catalina.base}/myconfig.properties
```

or in `application.groovy` like this:

```
grails.config.locations = [
        "classpath:myconfig.groovy",
        "classpath:myconfig.yml",
        "classpath:myconfig.properties",
        "file:///etc/app/myconfig.groovy",
        "file:///etc/app/myconfig.yml",
        "file:///etc/app/myconfig.properties",
        "~/.grails/myconfig.groovy",
        "~/.grails/myconfig.yml",
        "~/.grails/myconfig.properties",
        'file:${catalina.base}/myconfig.groovy',
        'file:${catalina.base}/myconfig.yml',
        'file:${catalina.base}/myconfig.properties',
]
```

You may also include external configs using '-D' arguments which match the system properties
the application is seeking, preceded by an application prefix determined by *info.app.name* in your default *application.yml*, *application.groovy*  or "app" if none exists. By default the following should work:

```
-DappName.config="/path/to/config"
-DappName.external.config="/path/to/config"
-DappName.database.config="/path/to/config"
-DappName.logging.config="/path/to/config"

```

or using JNDI variables *CONFIG*, *EXTERNAL_CONFIG*, *LOGGING_CONFIG*, *DATABASE_CONFIG* in tomcat for example:

```xml
<Context path="" docBase="/path/to/app.war"  reloadable="false">
        <Environment name="APP_CONFIG"
                value="file:/path/to/external_config.groovy"
                type="java.lang.String"/>
        <Environment name="DATABASE_CONFIG"
                value="file:/path/to/external_config.database.groovy"
                type="java.lang.String"/>
</Context>
```

While  no-longer necessary as of version 1.1.0 of [external-config](http://plugins.grails.org/plugin/grails/external-config), which this fork is based, for comparability you  may edit your Grails projects `Application.groovy` and implement the trait `com.neilab.plugins.config.ApplicationConfig` (formally ExternalConfig): 

```
import com.neilab.plugins.config.ApplicationConfig

class Application extends GrailsAutoConfiguration implements ApplicationConfig {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
```

This above is necessary only when *ApplicationConfigRunListener* is not executed, and   personally  have used it only when developing the plugin as an inline plugin where *SpringApplicationRunListener* was not loaded.


Notes
-----
Notice, that `~/` references the users `$HOME` directory.
Notice, that using a system property you should use single quotes because otherwise it's interpreted as a Gstring.

The plugin will skip configuration files that are not found. 

For `.groovy` and `.yml` files the `environments` blocks in the config file are interpreted the same way, as in `application.yml` or `application.groovy`.

Alternatively, you can make a gradle script to move the external configuration file to your classpath (e.g. /build/classes)

**Using IntelliJ or gradle to specify configurations via system properties**

Passing system properties via *VM Options* in IntelliJ or *-D* properties via gradle, it may be necessary to assign the parameters to the app via *bootRun* in your *build.gradle* configuration.

```groovy
//build.gradle
bootRun {
    systemProperties = System.properties
}
```

Scripts
-----
This plugin also includes two scripts, one for converting yml config, to groovy config,
and one for converting groovy config to yml config. These scripts are not guaranteed to be 
perfect, but you should report any edge cases for the yml to groovy config here:
https://github.com/virtualdogbert/GroovyConfigWriter/issues

Sample usage:
```
grails yml-to-groovy-config [ymlFile] [optional outputFile]
grails groovy-to-yml-config [ymlFile] [optional outputFile]
```
