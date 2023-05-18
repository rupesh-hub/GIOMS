package custom.keycloak.auth.resetPassword.initialLogin;

import custom.keycloak.auth.userspi.DefaultPasswordEncoderFactories;
import custom.keycloak.mapper.UserMapper;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.validation.Validation;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.passay.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@JBossLog
public class InitialLoginAuthenticator implements Authenticator {

    private final KeycloakSession session;
    private static final String TPL_CODE = "initial-login.ftl";
    private static final String TPL_CODE_EXPIRED = "login-page-expired.ftl";

    public InitialLoginAuthenticator(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        UserModel userModel = context.getUser();
        String userName = userModel.getUsername();

        ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
        WebApplicationContext appCtxt = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        UserMapper userMapper = appCtxt.getBean(UserMapper.class);

        Boolean isPasswordChange = userMapper.getIsPasswordChange(userName);
        if(isPasswordChange==null)isPasswordChange=false;
        if(isPasswordChange){
            context.success();
            return;
        }
        int ttl = 300;
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        authSession.setAuthNote("ttl", Long.toString(System.currentTimeMillis() + (ttl * 1000)));
        authSession.setAuthNote("username", userName);

        try {
            context.challenge(context.form().setAttribute("realm", context.getRealm()).createForm(TPL_CODE));
        } catch (Exception e) {
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                    context.form().setError("smsAuthSmsNotSent", e.getMessage())
                            .createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
        }
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
        // NOOP
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        String ttl = authSession.getAuthNote("ttl");

        if (ttl == null) {
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                    context.form().createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
            return;
        }

        AuthenticationExecutionModel execution = context.getExecution();
        if (execution.isRequired()) {
            if (Long.parseLong(ttl) < System.currentTimeMillis()) {
                // expired
                Response challenge = context.form()
                        .setAttribute("realm", context.getRealm())
                        .setError("timeExpire")
                        .createForm(TPL_CODE_EXPIRED);
                context.challenge(challenge);
//                context.failureChallenge(AuthenticationFlowError.EXPIRED_CODE,
//                        context.form()
//                                .setAttribute("realm", context.getRealm())
//                                .setError("timeExpire")
//                                .createForm(TPL_CODE));
                return;
            } else {
                // valid
                MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
                String passwordOld = formData.getFirst("password-old");
                String passwordNew = formData.getFirst("password-new");
                String passwordConfirm = formData.getFirst("password-confirm");

                if (Validation.isBlank(passwordOld)) {
                    Response challenge = context.form()
                            .setAttribute("realm", context.getRealm())
                            .setError("oldPasswordEmpty")
                            .createForm(TPL_CODE);
                    context.challenge(challenge);
                    return;
                }
                if (Validation.isBlank(passwordNew)) {
                    Response challenge = context.form()
                            .setAttribute("realm", context.getRealm())
                            .setError("newPasswordEmpty")
                            .createForm(TPL_CODE);
                    context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, challenge);
                    return;
                }
                else if (!passwordNew.equals(passwordConfirm)) {
                    Response challenge = context.form()
                            .setAttribute("realm", context.getRealm())
                            .setError("confirmPasswordNotEqual")
                            .createForm(TPL_CODE);
                    context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, challenge);
                    return;
                }

                if (!this.validatePasswordStrength(passwordNew)) {
                    Response challenge = context.form()
                            .setAttribute("realm", context.getRealm())
                            .setError("newPasswordValidate")
                            .createForm(TPL_CODE);
                    context.challenge(challenge);
                    return;
                }

                UserModel userModel = context.getUser();
                String userName = null;
                if(userModel.getUsername()!=null){
                    userName = userModel.getUsername();
                }else {
                    userName = authSession.getAuthNote("username");
                }
                if(userName==null){
                    Response challenge = context.form()
                            .setAttribute("realm", context.getRealm())
                            .setError("timeExpire")
                            .createForm(TPL_CODE_EXPIRED);
                    context.challenge(challenge);
                    return;
                }
                ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
                WebApplicationContext appCtxt = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
                UserMapper userMapper = appCtxt.getBean(UserMapper.class);
                String dbOld = userMapper.getUserPasswordHash(userName);
                if(!this.validatePassword(passwordOld, dbOld)){
                    Response challenge = context.form()
                            .setAttribute("realm", context.getRealm())
                            .setError("oldPasswordDoesntMatch")
                            .createForm(TPL_CODE);
                    context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, challenge);
                    return;
                }
                userMapper.updatePasswordAndStatus(this.encodePassword(passwordNew), userName);
                context.success();
            }
        } else if (execution.isConditional() || execution.isAlternative()) {
            context.attempted();
        }
    }

    private boolean validatePasswordStrength(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 30),
                new UppercaseCharacterRule(1),
                new DigitCharacterRule(1),
                new SpecialCharacterRule(1),
//                new NumericalSequenceRule(3,false),
//                new AlphabeticalSequenceRule(3,false),
//                new QwertySequenceRule(3,false),
                new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        return false;
    }

    private String encodePassword(String password) {
        return DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
    }

    private boolean validatePassword(String passwordOld, String dbOld) {
        return DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder().matches(passwordOld, dbOld);
    }

    @Override
    public void close() {
        // NOOP
    }
}
