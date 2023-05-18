package custom.keycloak.auth.config;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class ConfigurableTokenResourceProviderFactory implements RealmResourceProviderFactory {
    private static final Logger LOG = Logger.getLogger(ConfigurableTokenResourceProviderFactory.class);

    public RealmResourceProvider create(KeycloakSession session) {
        ConfigurationTokenResourceConfiguration configuration = ConfigurationTokenResourceConfiguration.readFromEnvironment();
        LOG.infof("Keycloak-ConfigurableToken is configured with: %s", configuration);
        return new ConfigurableTokenResourceProvider(session, configuration);
    }

    public void init(Config.Scope config) {}

    public void postInit(KeycloakSessionFactory factory) {}

    public void close() {}

    public String getId() {
        return "configurable-token";
    }
}
