package custom.keycloak.auth.userspi;

import com.google.auto.service.AutoService;
import custom.keycloak.cache.TokenCacheRedisRepo;
import custom.keycloak.service.UserService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * Keycloak file based user storage provider factory
 *
 */
@JBossLog
@AutoService(org.keycloak.storage.UserStorageProviderFactory.class)
public class UserStorageProviderFactory implements org.keycloak.storage.UserStorageProviderFactory<UserStorageProvider> {

    @Override
    public void init(Config.Scope config) {
        String someProperty = config.get("someProperty");
        log.infov("Configured {0} with someProperty: {1}", this, someProperty);
    }

    @Override
    public UserStorageProvider create(KeycloakSession session, ComponentModel model) {
        ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
        WebApplicationContext appCtxt = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        UserService userService = appCtxt.getBean(UserService.class);
        PasswordEncoder passwordEncoder = appCtxt.getBean(PasswordEncoder.class);
        TokenCacheRedisRepo tokenCacheRedisRepo = appCtxt.getBean(TokenCacheRedisRepo.class);
        return new UserStorageProvider(session, model, userService, passwordEncoder, tokenCacheRedisRepo);
    }

    @Override
    public String getId() {
        return "external-user-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        // this configuration is configurable in the admin-console
        return ProviderConfigurationBuilder.create()
                .property()
                .name("myParam")
                .label("My Param")
                .helpText("Some Description")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("some value")
                .add()
                .build();
    }
}
