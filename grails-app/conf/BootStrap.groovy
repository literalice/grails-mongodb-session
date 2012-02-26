import com.monochromeroad.grails.plugins.mongodbsession.sample.User
import com.monochromeroad.grails.plugins.mongodbsession.sample.Role
import com.monochromeroad.grails.plugins.mongodbsession.MongoPersistentSession

class BootStrap {

    def springSecurityService

    def init = { servletContext ->
        MongoPersistentSession.collection.remove([:])

        User.collection.remove([:])
        def username = "masatoshi"
        new User(username: username,
                 password: springSecurityService.encodePassword(username, username),
                 authorities: [new Role(authority: "ROLE_USER")] as Set<Role>,
                 enabled: true).save(failOnError: true)
    }

}
