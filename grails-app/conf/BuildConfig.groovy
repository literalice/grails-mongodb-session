grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
    }
    dependencies {
    }

    plugins {
        build(":svn:1.0.2", ":release:1.0.1") {
            export = false
        }
        compile(":mongodb:1.0.0.RC4", ":database-session:1.1.2", ":webxml:1.4.1")
        test(":spock:0.6-SNAPSHOT") {
            export = false
        }
    }
}
