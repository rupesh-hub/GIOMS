package custom.keycloak.auth.resetPassword;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.authenticators.resetcred.AbstractSetRequiredActionAuthenticator;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;

@JBossLog
@AutoService(AuthenticatorFactory.class)
public class ResetPassword extends AbstractSetRequiredActionAuthenticator {

    public static final String PROVIDER_ID = "custom-reset-password";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (context.getExecution().isRequired() ||
                (context.getExecution().isConditional() &&
                        configuredFor(context))) {
            context.getAuthenticationSession().addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
        }
        context.success();
    }

    protected boolean configuredFor(AuthenticationFlowContext context) {
        UserModel userModel = context.getUser();
        return context.getSession().userCredentialManager().isConfiguredFor(context.getRealm(), userModel, PasswordCredentialModel.TYPE);
    }

    @Override
    public String getDisplayType() {
        return "Custom Reset Password";
    }

    @Override
    public String getHelpText() {
        return "Sets the Update Password required action if execution is REQUIRED.  Will also set it if execution is OPTIONAL and the password is currently configured for it.";
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
