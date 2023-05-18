package custom.keycloak.auth.resetPassword;

import com.google.auto.service.AutoService;
import custom.keycloak.auth.userspi.DefaultPasswordEncoderFactories;
import custom.keycloak.mapper.UserMapper;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.keycloak.services.validation.Validation;
import org.keycloak.storage.StorageId;
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
@AutoService(RequiredActionFactory.class)
public class UpdatePassword implements RequiredActionProvider, RequiredActionFactory {

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
//        int daysToExpirePassword = context.getRealm().getPasswordPolicy().getDaysToExpirePassword();
//        if(daysToExpirePassword != -1) {
//            for (UserCredentialValueModel entity : context.getUser().getCredentialsDirectly()) {
//                if (entity.getType().equals(UserCredentialModel.PASSWORD)) {
//
//                    if(entity.getCreatedDate() == null) {
//                        context.getUser().addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
//                        log.debug("User is required to update password");
//                    } else {
//                        long timeElapsed = Time.toMillis(Time.currentTime()) - entity.getCreatedDate();
//                        long timeToExpire = TimeUnit.DAYS.toMillis(daysToExpirePassword);
//
//                        if(timeElapsed > timeToExpire) {
//                            context.getUser().addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
//                            log.debug("User is required to update password");
//                        }
//                    }
//                    break;
//                }
//            }
//        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        Response challenge = context.form()
                .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        EventBuilder event = context.getEvent();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        event.event(EventType.UPDATE_PASSWORD);
        String passwordNew = formData.getFirst("password-new");
        String passwordConfirm = formData.getFirst("password-confirm");

        EventBuilder errorEvent = event.clone().event(EventType.UPDATE_PASSWORD_ERROR)
                .client(context.getSession().getContext().getClient())
                .user(context.getUser());

        if (Validation.isBlank(passwordNew)) {
            Response challenge = context.form()
                    .setError("newPasswordEmpty")
                    .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.challenge(challenge);
            errorEvent.error(Errors.PASSWORD_MISSING);
            return;
        }
        else if (!passwordNew.equals(passwordConfirm)) {
            Response challenge = context.form()
                    .setError("confirmPasswordNotEqual")
                    .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.challenge(challenge);
            errorEvent.error(Errors.PASSWORD_CONFIRM_ERROR);
            return;
        }

        if (!this.validatePasswordStrength(passwordNew)) {
            Response challenge = context.form()
                    .setError("newPasswordValidate")
                    .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.challenge(challenge);
            errorEvent.error(Errors.PASSWORD_CONFIRM_ERROR);
            return;
        }

        try {
            UserModel user = context.getUser();
            String userName = null;

            ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
            WebApplicationContext appCtxt = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            UserMapper userMapper = appCtxt.getBean(UserMapper.class);
            if(user.getUsername()!=null){
                userName = user.getUsername();
            }else {
                StorageId storageId = new StorageId(context.getEvent().getEvent().getUserId());
                userName = storageId.getExternalId();
            }
            userMapper.updatePassword(this.encodePassword(passwordNew), userName);
            context.success();

            KeycloakSession session = context.getSession();

            RealmModel realm = context.getRealm();

            // user has proven that he owns this account
            UserLoginFailureModel userLoginFailure = session.sessions().getUserLoginFailure(realm, user.getId());
            if (userLoginFailure != null && userLoginFailure.getNumFailures() > 0) {
               userLoginFailure.clearFailures();
            }

        } catch (ModelException me) {
            errorEvent.detail(Details.REASON, me.getMessage()).error(Errors.PASSWORD_REJECTED);
            Response challenge = context.form()
                    .setError(me.getMessage(), me.getParameters())
                    .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.challenge(challenge);
            return;
        } catch (Exception ape) {
            errorEvent.detail(Details.REASON, ape.getMessage()).error(Errors.PASSWORD_REJECTED);
            Response challenge = context.form()
                    .setError(ape.getMessage())
                    .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.challenge(challenge);
            return;
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

    @Override
    public void close() {

    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getDisplayText() {
        return "Custom Update Password";
    }


    @Override
    public String getId() {
        return UserModel.RequiredAction.UPDATE_PASSWORD.name();
    }
}
