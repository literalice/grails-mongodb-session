// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.useSessionFixationPrevention = true
grails.plugins.springsecurity.userLookup.userDomainClassName = "com.monochromeroad.grails.plugins.mongodbsession.sample.User"
grails.plugins.springsecurity.userLookup.authorityJoinClassName = "com.monochromeroad.grails.plugins.mongodbsession.sample.UserRole"
grails.plugins.springsecurity.authority.className = "com.monochromeroad.grails.plugins.mongodbsession.sample.Role"
grails.plugins.springsecurity.dao.reflectionSaltSourceProperty = "username"
grails.plugins.springsecurity.logout.afterLogoutUrl="/secure"

// Database Session
grails.plugin.databasesession.enabled = true
grails.plugin.databasesession.deleteInvalidSessions = true

