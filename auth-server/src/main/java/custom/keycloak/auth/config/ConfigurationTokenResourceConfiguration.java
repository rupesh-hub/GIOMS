package custom.keycloak.auth.config;

public class ConfigurationTokenResourceConfiguration {
    private static final String KEYCLOAK_LONG_LIVED_ROLE_NAME = "KEYCLOAK_LONG_LIVED_ROLE_NAME";

    private static final String DEFAULT_KEYCLOAK_LONG_LIVED_ROLE_NAME = "long_lived_token";

    private final String longLivedTokenRole;

    public static ConfigurationTokenResourceConfiguration readFromEnvironment() {
        String longLivedTokenRole = readLongLivedRoleFromEnvironment();
        return new ConfigurationTokenResourceConfiguration(longLivedTokenRole);
    }

    public ConfigurationTokenResourceConfiguration(String longLivedTokenRole) {
        this.longLivedTokenRole = longLivedTokenRole;
    }

    public String getLongLivedTokenRole() {
        return this.longLivedTokenRole;
    }

    public String toString() {
        return "longLivedTokenRole=" + this.longLivedTokenRole;
    }

    private static String readLongLivedRoleFromEnvironment() {
        String roleForLongLivedTokens = System.getenv("KEYCLOAK_LONG_LIVED_ROLE_NAME");
        if (roleForLongLivedTokens == null || roleForLongLivedTokens.trim().isEmpty())
            return "long_lived_token";
        return roleForLongLivedTokens;
    }
}
