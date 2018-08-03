package com.neilab.plugins.config

import grails.plugins.*

class ApplicationConfigGrailsPlugin extends Plugin {

    def grailsVersion = "3.0.0 > *"
    def pluginExcludes = []

    def title = "Application Config"
    def author = "neilabdev"
    def authorEmail = "neilabdev@users.noreply.github.com"
    def description = '''\
Load configs with grails.config.locations like in Grails 2.x
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "https://github.com/neilabdev/application-config"
    def license = "APACHE"

    // Any additional developers beyond the author specified above.
    def developers = [
        [ name: "neilabdev", url: "https://github.com/neilabdev" ],
    ]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/neilabdev/application-config/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/neilabdev/application-config" ]
}
