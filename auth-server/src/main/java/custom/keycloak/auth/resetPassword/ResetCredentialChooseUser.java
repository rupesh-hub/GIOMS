package custom.keycloak.auth.resetPassword;

import com.google.auto.service.AutoService;
import custom.keycloak.mapper.UserMapper;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.actiontoken.DefaultActionTokenKey;
import org.keycloak.authentication.authenticators.broker.AbstractIdpAuthenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

@JBossLog
@AutoService(AuthenticatorFactory.class)
public class ResetCredentialChooseUser implements Authenticator, AuthenticatorFactory {


    public static final String PROVIDER_ID = "custom-reset-credentials-choose-user";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String existingUserId = context.getAuthenticationSession().getAuthNote(AbstractIdpAuthenticator.EXISTING_USER_INFO);
        if (existingUserId != null) {
            UserModel existingUser = AbstractIdpAuthenticator.getExistingUser(context.getSession(), context.getRealm(), context.getAuthenticationSession());

            log.debugf("Forget-password triggered when reauthenticating user after first broker login. Prefilling reset-credential-choose-user screen with user '%s' ", existingUser.getUsername());
            context.setUser(existingUser);
            Response challenge = context.form().createPasswordReset();
            context.challenge(challenge);
            return;
        }

        String actionTokenUserId = context.getAuthenticationSession().getAuthNote(DefaultActionTokenKey.ACTION_TOKEN_USER_ID);
        if (actionTokenUserId != null) {
            UserModel existingUser = context.getSession().users().getUserById(actionTokenUserId, context.getRealm());

            // Action token logics handles checks for user ID validity and user being enabled

            log.debugf("Forget-password triggered when reauthenticating user after authentication via action token. Skipping reset-credential-choose-user screen and using user '%s' ", existingUser.getUsername());
            context.setUser(existingUser);
            context.success();
            return;
        }

        Response challenge = context.form().createPasswordReset();
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        EventBuilder event = context.getEvent();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String username = formData.getFirst("username");
        if (username == null || username.isEmpty()) {
            event.error(Errors.USERNAME_MISSING);
            Response challenge = context.form()
                    .addError(new FormMessage(Validation.FIELD_USERNAME, Messages.MISSING_USERNAME))
                    .createPasswordReset();
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
            return;
        }

        username = username.trim();
        
        RealmModel realm = context.getRealm();
        UserModel user = context.getSession().users().getUserByUsername(username, realm);

//        if (user == null && realm.isLoginWithEmailAllowed() && username.contains("@")) {
//            user =  context.getSession().users().getUserByEmail(username, realm);
//        }

        context.getAuthenticationSession().setAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME, username);

        // we don't want people guessing usernames, so if there is a problem, just continue, but don't set the user
        // a null user will notify further executions, that this was a failure.
        if (user == null) {
//            event.clone()
//                    .detail(Details.USERNAME, username)
//                    .error(Errors.USER_NOT_FOUND);
            context.clearUser();

            event.error(Errors.USER_NOT_FOUND);
            Response challenge = context.form()
                    .addError(new FormMessage(Validation.FIELD_USERNAME, "userNotFound"))
                    .createPasswordReset();
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
            return;
        } else if (!user.isEnabled()) {
//            event.clone()
//                    .detail(Details.USERNAME, username)
//                    .user(user).error(Errors.USER_DISABLED);
            context.clearUser();

            event.error(Errors.USER_DISABLED);
            Response challenge = context.form()
                    .addError(new FormMessage(Validation.FIELD_USERNAME, Messages.ACCOUNT_DISABLED))
                    .createPasswordReset();
            context.failureChallenge(AuthenticationFlowError.USER_DISABLED, challenge);
            return;
        } else {
            ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
            WebApplicationContext appCtxt = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            UserMapper userMapper = appCtxt.getBean(UserMapper.class);
            String email = userMapper.getEmail(user.getUsername());
            user.setEmail(email);
            context.setUser(user);
        }
        context.success();
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public String getDisplayType() {
        return "Custom Choose User";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Choose a user to reset credentials for";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
