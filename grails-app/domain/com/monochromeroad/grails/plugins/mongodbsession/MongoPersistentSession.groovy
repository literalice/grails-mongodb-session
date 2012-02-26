package com.monochromeroad.grails.plugins.mongodbsession

import com.mongodb.BasicDBObject
import grails.plugin.databasesession.InvalidatedSessionException

class MongoPersistentSession {

    static byte[] getAttribute(String sessionId, String name) {
        if (name == null) return null

        def session = MongoPersistentSession.collection.findAndModify(
                bo([_id: sessionId]), bo([$set: [lastAccessedTime: System.currentTimeMillis()]]))
        checkInvalidated(session)

        def sName = name.split(/\./)
        def value = session.attributes[sName[0]]
        (sName.size() - 1).times {
            if (value instanceof Map) {
                value = value."${sName[it + 1]}"
            }
        }
        return value
    }

    static void setAttribute(String sessionId, String name, byte[] value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException('name parameter cannot be null')
        }

        def attributeName = "attributes.$name".toString()
        MongoPersistentSession.collection.update(
                bo([_id: sessionId]), bo([$set: [(attributeName): value]]))
    }

    static void removeAttribute(String sessionId, String name) throws InvalidatedSessionException {
        if (name == null) return

        def attributeName = "attributes.$name".toString()
        MongoPersistentSession.collection.update(
                bo([_id: sessionId]), bo([$unset: [(attributeName): 1]]))
    }

    static List<String> getAttributeNames(String sessionId) throws InvalidatedSessionException {
        def session = MongoPersistentSession.collection.findOne([_id: sessionId], [attributes: 1])
        def keySet = session.attributes.keySet() as Set<String>
        if (keySet) {
            return new ArrayList<String>(keySet)
        } else {
            return []
        }
    }

    static void invalidate(String sessionId, Boolean delete) {
        if (delete) {
            MongoPersistentSession.collection.remove(bo([_id: sessionId]))
        } else {
            MongoPersistentSession.collection.update(
                    bo([_id: sessionId]), bo([$set: [invalidated: true]]))
        }
    }

    static long getLastAccessedTime(String sessionId) throws InvalidatedSessionException {
        MongoPersistentSession ps = MongoPersistentSession.get(sessionId)
        checkInvalidated ps
        ps.lastAccessedTime
    }

    static void setMaxInactiveInterval(String sessionId, int interval) throws InvalidatedSessionException {
        MongoPersistentSession.collection.update(
                bo([_id: sessionId]), bo([$set: [maxInactiveInterval: interval]]))
    }

    static int getMaxInactiveInterval(String sessionId) throws InvalidatedSessionException {
        MongoPersistentSession ps = MongoPersistentSession.get(sessionId)
        checkInvalidated ps
        ps.maxInactiveInterval
    }

    static void removeAllByLastAccessedOlderThan(long age) {
        MongoPersistentSession.collection.remove(["lastAccessedTime": [$lt: age]])
    }

    private static BasicDBObject bo(Map source) {
        new BasicDBObject(source)
    }

    private static void checkInvalidated(session) {
        if (!session || session.invalidated) {
            throw new InvalidatedSessionException()
        }
    }

    static mapWith = "mongo"

    static mapping = {
        id generator: "assigned"
        version false
        lastAccessedTime index: true
    }

    String id

    Long creationTime

    Long lastAccessedTime

    Boolean invalidated = false

    Integer maxInactiveInterval = 30

    Map<String, byte[]> attributes = [:]

    boolean isValid() {
        !invalidated && lastAccessedTime > System.currentTimeMillis() - maxInactiveInterval * 1000 * 60
    }
}
