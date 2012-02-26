package com.monochromeroad.grails.plugins.mongodbsession.sample

/**
 * Sample Login User
 * @author Masatoshi Hayashi
 */
class User {

    static mapWith = "mongo"

    static embedded = ["authorities"]

    String username = ""

    String password = ""

    boolean enabled = true

    boolean accountExpired = false

    boolean accountLocked = false

    boolean passwordExpired = false

    Set<Role> authorities = [] as Set

    Date dateCreated = new Date()
}

