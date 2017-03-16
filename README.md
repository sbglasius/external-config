External-Config
===============
[![Build Status](https://travis-ci.org/sbglasius/external-config.svg?branch=master)](https://travis-ci.org/sbglasius/external-config)

This plugin will mimic the Grails 2 way of handling external configurations defined in `grails.config.locations`.


IMPORTANT!
----------
The External Config Plugin no longer needs to implement `ExternalConfig` on `Application.groovy`. It now uses a `SpringApplicationRunListener`and hooks into the startup automagically. So if you used the plugin in prior versions, please remove `implements ExternalConfig` from `Application.groovy`

Thanks you to [Sudhir Nimavat](https://github.com/snimavat) for the Pull Request!

Installation
------------

Add dependency to your `build.gradle`:

```
dependencies {
    compile 'org.grails.plugins:external-config:1.0.0'
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

Notice, that `~/` references the users `$HOME` directory.
Notice, that using a system property you should use single quotes because otherwise it's interpreted as a Gstring.

The plugin will skip configuration files that are not found. 

For `.groovy` and `.yml` files the `environments` blocks in the config file are interpreted the same way, as in `application.yml` or `application.groovy`.



