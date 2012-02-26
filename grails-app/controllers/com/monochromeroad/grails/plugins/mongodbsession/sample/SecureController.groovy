package com.monochromeroad.grails.plugins.mongodbsession.sample

import grails.plugins.springsecurity.Secured

/**
 * Sample Secure Controller
 * @author Masatoshi Hayashi
 */
class SecureController {

    @Secured(["ROLE_USER"])
    def index = {}

}
