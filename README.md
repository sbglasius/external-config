# External-Config

[![Build Status](https://travis-ci.org/sbglasius/external-config.svg?branch=master)](https://travis-ci.org/sbglasius/external-config)

This plugin will mimic the Grails 2 way of handling external configurations defined in `grails.config.locations`. 

It also provides scripts to convert between yml & groovy config.

## Versions

| Grails | external-config |
|--|--|
| 4.x.x | 2.0.0 |
| 3.3.x | 1.4.0 |

## Contributors

Major contributors

* [Jesper Steen Møller](https://github.com/jespersm)
* [Tucker J. Pelletier](https://github.com/virtualdogbert)
* [Dennie de Lange](https://github.com/tkvw)
* [Sudhir Nimavat](https://github.com/snimavat) 
* [Anders Aaberg](https://github.com/andersaaberg)

Thank you!

## Installation

Add dependency to your `build.gradle`:

```groovy
dependencies {
    compile 'org.grails.plugins:external-config:2.0.0' // or latest version
}
```

### Snapshot

To use a snapshot-version

add JFrog OSS Repository to the `repositories`:

```groovy
repositories {
    maven { url "https://oss.jfrog.org/repo/" }
}
```

and specify the snapshot version as a dependency:

```groovy
dependencies {
    compile 'org.grails.plugins:external-config:2.0.1.BUILD-SNAPSHOT'
}
```

## Usage

When you add this plugin to your Grails build, it will automatically look for the property `grails.config.locations`. Define this in in either `application.yml` like this:

```yml
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
        "~/.grails/myconfig.properties",
        'file:${catalina.base}/myconfig.groovy',
        'file:${catalina.base}/myconfig.yml',
        'file:${catalina.base}/myconfig.properties',
]
```

It is also possible to define it in an environment specific block (yml):


```yml
environments:
    test:
        grails:
            config:
                locations:
                - ... 
```

or (groovy)

```groovy
environments {
    test {
        grails {
            config {
                locations = [...]
            }
        }
    }
}   
```

`~/` references the users `$HOME` directory.
Notice, that using a system property you should use single quotes because otherwise it's interpreted as a Gstring.

The plugin will skip configuration files that are not found. 

For `.groovy` and `.yml` files the `environments` blocks in the config file are interpreted the same way, as in `application.yml` or `application.groovy`.

### Wildcard support

It is possible to use `*` as wildcards in the filename part of the configuration:

```yaml
grails:
    config:
        locations:
            - file:/etc/app/myconfig*.groovy
            - ~/.grails/myconfig*.groovy
```
or
```groovy
grails.config.locations = [
        "file:/etc/app/myconfig*.groovy",
        "~/.grails/myconfig*.groovy",
]
```
__Note__: that it only works for the `file:` and `~/` prefix. 

__Note__: the wildcards are in the order they are found in the `locations` list, but the order of the expanded `locations` for each wildcard is not guaranteed, and is dependent on the OS used.

### Getting configuration from another folder than /conf on classpath without moving it with Gradle script

If you wish to make your Grails application pull external configuration from classpath when running locally, but you do not wish to get it packed into the assembled war file (i.e. place the external configuration file in e.g. /external-config instead of /conf), then you can include the external configuration file to the classpath by adding the following line to build.gradle:dependencies
```groovy
provided files('external-config') // provided to ensure that external config is not included in the war file
```
Alternatively, you can make a gradle script to move the external configuration file to your classpath (e.g. /build/classes)

## Scripts

This plugin also includes two scripts, one for converting yml config, to groovy config,
and one for converting groovy config to yml config. These scripts are not guaranteed to be 
perfect, but you should report any edge cases for the yml to groovy config here:
https://github.com/virtualdogbert/GroovyConfigWriter/issues

`grails yml-to-groovy-config` has the following parameters:
* ymlFile - The yml input file.
* asClosure - An optional flag to set the output to be closure based or map based. The Default is closure based 
* outputFile - The optional output file. If none is provided, then the output will go to System.out.
* indent - Optional indent. The default is 4 spaces
* escapeList - An optional CSV list of values to escape, with no spaces. The default is 'default'


### Sample usage

```
grails yml-to-groovy-config [ymlFile] [optional asClosure] [optional outputFile] [optional indent] [optional escape list]
```

`grails groovy-to-yml-config` has the following parameters:
* groovy - The groovy input file.'
* outputFile - The optional output file. If none is provided, then the output will go to System.out.
* indent' - Sets the optional indent level for a file. The default is 4
* flow - Sets the optional style of BLOCK or FLOWS. The default is BLOCK.

### Sample usage

```
grails groovy-to-yml-config [ymlFile] [optional outputFile] [optional indent] [optinal flow]
```
