package custom.keycloak.auth.userspi;

import custom.keycloak.model.User;
import custom.keycloak.service.UserService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.AbstractKeycloakTransaction;

/**
 * Custom Keycloak transaction for file based user repository
 */

@JBossLog
public class Transaction extends AbstractKeycloakTransaction {

    private final UserService userService;
    private final User user;

    public Transaction(UserService userService, User user) {
        this.userService = userService;
        this.user = user;
    }

    @Override
    protected void commitImpl() {
        log.infov("Updating user to external repository in a transaction.");
        user.setPassword(user.getUsername()); // surely this needs to be more securely handled
        log.infov("User to be updated into the repository: {0}", user.toString());
        userService.updateUser(user);
    }

    @Override
    protected void rollbackImpl() {
        log.infov("Rolling back data change to external user repository ...");
    }
}
