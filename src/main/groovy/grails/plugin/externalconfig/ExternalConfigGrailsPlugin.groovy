package grails.plugin.externalconfig

import grails.plugins.*

class ExternalConfigGrailsPlugin extends Plugin {

    def grailsVersion = "3.0.0 > *"
    def pluginExcludes = []

    def title = "External Config" // Headline display name of the plugin
    def author = "Søren Berg Glasius"
    def authorEmail = "soeren@glasius.dk"
    def description = '''\
Load configs with grails.config.locations like in Grails 2.x
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/external-config"
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    //    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]
}
