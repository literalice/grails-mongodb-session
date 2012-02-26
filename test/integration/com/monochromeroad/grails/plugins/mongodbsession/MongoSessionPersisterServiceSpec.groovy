package com.monochromeroad.grails.plugins.mongodbsession

import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser

/**
 * Spec for the mongo persister
 *
 * @author Masatoshi Hayashi
 */
class MongoSessionPersisterServiceSpec extends IntegrationSpec {

    def grailsApplication

    def mongoSessionPersisterService

    def setup() {
        MongoPersistentSession.collection.remove([:])
    }

    def "Creates a session"() {
        def sessionId = UUID.randomUUID().toString()

        when:
        mongoSessionPersisterService.create(sessionId)

        then:
        MongoPersistentSession.exists(sessionId)
    }

    def "Accesses to a attribute of a session"(def key) {
        def sessionId = UUID.randomUUID().toString()
        def username = "masatoshi"
        def value = new GrailsUser(username, "passwd", true, false, false, false, [], "1")

        when:
        mongoSessionPersisterService.create(sessionId)
        mongoSessionPersisterService.setAttribute(sessionId, key, value)
        and:
        def sessionValue = mongoSessionPersisterService.getAttribute(sessionId, key)

        then:
        sessionValue instanceof GrailsUser
        sessionValue.id == value.id
        sessionValue.username == username

        where:
        key << ["user", "session.user"]
    }

    def "Removes a attribute of a session"() {
        def sessionId = UUID.randomUUID().toString()
        def key = "sValue"
        def value = "Session Value"

        when:
        mongoSessionPersisterService.create(sessionId)
        mongoSessionPersisterService.setAttribute(sessionId, key, value)
        and:
        def sessionValue = mongoSessionPersisterService.getAttribute(sessionId, key)
        assert sessionValue == value
        and:
        mongoSessionPersisterService.removeAttribute(sessionId, key)

        then:
        mongoSessionPersisterService.getAttribute(sessionId, key) == null
    }

    def "Enums all attribute names"() {
        def sessionId = UUID.randomUUID().toString()
        def keys = ["key1", "key2", "key3"]

        when:
        mongoSessionPersisterService.create(sessionId)
        for (key in keys) {
            mongoSessionPersisterService.setAttribute(sessionId, key, "testValue")
        }
        and:
        def sessionKeys = mongoSessionPersisterService.getAttributeNames(sessionId)
        then:
        for (key in keys) {
            assert sessionKeys.any { it == key }
        }
    }

    def "Invalidates a session"(delete) {
        grailsApplication.config.grails.plugin.databasesession.deleteInvalidSessions = delete
        def sessionId = UUID.randomUUID().toString()

        when:
        mongoSessionPersisterService.create(sessionId)
        assert !MongoPersistentSession.collection.findOne([_id: sessionId]).invalidated
        and:
        mongoSessionPersisterService.invalidate(sessionId)

        then:
        if (delete) {
            assert !MongoPersistentSession.collection.findOne([_id: sessionId])
        } else {
            assert MongoPersistentSession.collection.findOne([_id: sessionId]).invalidated
        }

        where:
        delete << [true, false]
    }
}
