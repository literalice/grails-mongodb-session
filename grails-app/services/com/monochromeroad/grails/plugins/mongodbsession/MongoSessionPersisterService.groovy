package com.monochromeroad.grails.plugins.mongodbsession

import grails.plugin.databasesession.InvalidatedSessionException
import grails.plugin.databasesession.Persister
import grails.util.GrailsUtil
import org.grails.datastore.mapping.validation.ValidationException

/**
 * the session persister for Mongo
 *
 * @author Masatoshi Hayashi
 */
class MongoSessionPersisterService implements Persister {

    static transactional = false

    def grailsApplication

    void create(String sessionId) {
        try {
            if (MongoPersistentSession.exists(sessionId)) {
                return
            }

            MongoPersistentSession session = new MongoPersistentSession()
            session.creationTime = System.currentTimeMillis()
            session.lastAccessedTime = session.creationTime
            session.id = sessionId
            session.save(failOnError: true, flush: true)
        }
        catch (e) {
            handleException e
        }
    }

    Object getAttribute(String sessionId, String name) throws InvalidatedSessionException {
        if (name == null) return null

        def attribute = null
        try {
            byte[] serialized = MongoPersistentSession.getAttribute(sessionId, name)
            if (serialized) {
                def valueStream = new ByteArrayInputStream(serialized)
                def grailsOs = new ObjectInputStream(valueStream) {

                    @Override
                    public Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        //noinspection GroovyUnusedCatchParameter
                        try {
                            return grailsApplication.classLoader.loadClass(desc.name)
                        } catch (ClassNotFoundException ex) {
                            return Class.forName(desc.name)
                        }
                    }

                }
                attribute = grailsOs.readObject()
            }
        } catch (e) {
            handleException e
        }

        return attribute
    }

    void setAttribute(String sessionId, String name, value) throws InvalidatedSessionException {
        if (name == null) {
            throw new IllegalArgumentException('name parameter cannot be null')
        }

        if (value == null) {
            removeAttribute sessionId, name
            return
        }

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream()
            // might throw IOException - let the caller handle it
            new ObjectOutputStream(stream).writeObject value
            byte[] serialized = stream.toByteArray()

            MongoPersistentSession.setAttribute(sessionId, name, serialized)
        } catch (e) {
            handleException e
        }
    }

    void removeAttribute(String sessionId, String name) throws InvalidatedSessionException {
        if (name == null) return

        try {
            MongoPersistentSession.removeAttribute(sessionId, name)
        } catch (e) {
            handleException e
        }
    }

    List<String> getAttributeNames(String sessionId) throws InvalidatedSessionException {
        List<String> names = []
        try {
            names = MongoPersistentSession.getAttributeNames(sessionId)
        } catch (e) {
            handleException e
        }
        return names
    }

    void invalidate(String sessionId) {
        try {
            def config = grailsApplication.config.grails.plugin.databasesession
            def deleteInvalidSessions = config.deleteInvalidSessions ?: false
            MongoPersistentSession.invalidate(sessionId, deleteInvalidSessions)
        } catch (e) {
            handleException e
        }
    }

    long getLastAccessedTime(String sessionId) throws InvalidatedSessionException {
        MongoPersistentSession.getLastAccessedTime(sessionId)
    }

    void setMaxInactiveInterval(String sessionId, int interval) throws InvalidatedSessionException {
        if (interval) {
            MongoPersistentSession.setMaxInactiveInterval(sessionId, interval)
        } else {
            invalidate sessionId
        }
    }

    int getMaxInactiveInterval(String sessionId) throws InvalidatedSessionException {
        MongoPersistentSession.getMaxInactiveInterval(sessionId)
    }

    boolean isValid(String sessionId) {
        MongoPersistentSession session = MongoPersistentSession.get(sessionId)
        if (session) {
            return session.isValid()
        } else {
            return false
        }
    }

    private void handleException(e) {
        if (e instanceof InvalidatedSessionException || e instanceof ValidationException) {
            throw e
        }
        GrailsUtil.deepSanitize e
        log.error e.message, e
    }
}

