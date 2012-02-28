import com.monochromeroad.grails.plugins.mongodbsession.MongoSessionCleanupService
import grails.plugin.databasesession.SessionProxyFilter

class MongodbSessionGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.3 > *"
    // the other plugins this plugin depends on
    def loadAfter = ["database-session"]

    def title = "MongoDB Session Plugin" // Headline display name of the plugin
    def author = "Masatoshi Hayashi"
    def authorEmail = "literalice@monochromeroad.com"
    def description = "Stores HTTP sessions in a MongoDB."

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/mongodb-session"

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMONGODBSESSION" ]

    // Online location of the plugin's browseable source code.
    def scm = [url: 'https://github.com/literalice/grails-mongodb-session']

    def doWithSpring = {
        sessionProxyFilter(SessionProxyFilter) {
            persister = ref('mongoSessionPersisterService')
        }
        databaseCleanupService(MongoSessionCleanupService) {
            grailsApplication = ref('grailsApplication')
        }
    }

}
