package custom.keycloak.auth.config;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.crypto.SignatureProvider;
import org.keycloak.crypto.SignatureVerifierContext;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.services.ErrorResponse;
import org.keycloak.services.Urls;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resources.Cors;
import org.keycloak.services.util.DefaultClientSessionContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

public class ConfigurableTokenResourceProvider implements RealmResourceProvider {
    static final String ID = "configurable-token";

    private static final Logger LOG = Logger.getLogger(ConfigurableTokenResourceProvider.class);

    private final KeycloakSession session;

    private final TokenManager tokenManager;

    private final ConfigurationTokenResourceConfiguration configuration;

    ConfigurableTokenResourceProvider(KeycloakSession session, ConfigurationTokenResourceConfiguration configuration) {
        this.session = session;
        this.tokenManager = new TokenManager();
        this.configuration = configuration;
    }

    public Object getResource() {
        return this;
    }

    public void close() {}

    @OPTIONS
    public Response preflight(@Context HttpRequest request) {
        return Cors.add(request, Response.ok()).auth().preflight().allowedMethods(new String[] { "POST", "OPTIONS" }).build();
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response createToken(TokenConfiguration tokenConfiguration, @Context HttpRequest request) {
        try {
            AccessToken accessToken = validateTokenAndUpdateSession(request);
            UserSessionModel userSession = findSession();
            AccessTokenResponse response = createAccessToken(userSession, accessToken, tokenConfiguration);
            return buildCorsResponse(request, response);
        } catch (ConfigurableTokenException e) {
            LOG.error("An error occurred when fetching an access token", e);
            return ErrorResponse.error(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    private AccessTokenResponse createAccessToken(UserSessionModel userSession, AccessToken accessToken, TokenConfiguration tokenConfiguration) {
        RealmModel realm = this.session.getContext().getRealm();
        ClientModel client = realm.getClientByClientId(accessToken.getIssuedFor());
        LOG.infof("Configurable token requested for username=%s and client=%s on realm=%s", userSession.getUser().getUsername(), client.getClientId(), realm.getName());
        AuthenticatedClientSessionModel clientSession = userSession.getAuthenticatedClientSessionByClient(client.getId());
        DefaultClientSessionContext defaultClientSessionContext = DefaultClientSessionContext.fromClientSessionScopeParameter(clientSession, this.session);
        AccessToken newToken = this.tokenManager.createClientAccessToken(this.session, realm, client, userSession.getUser(), userSession, (ClientSessionContext)defaultClientSessionContext);
        updateTokenExpiration(newToken, tokenConfiguration, userSession.getUser());
        return buildResponse(realm, userSession, client, clientSession, newToken);
    }

    private AccessToken validateTokenAndUpdateSession(HttpRequest request) throws ConfigurableTokenException {
        try {
            RealmModel realm = this.session.getContext().getRealm();
            String tokenString = readAccessTokenFrom(request);
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(tokenString, AccessToken.class).withChecks(new TokenVerifier.Predicate[] { TokenVerifier.IS_ACTIVE, (TokenVerifier.Predicate)new TokenVerifier.RealmUrlCheck(

                    Urls.realmIssuer(this.session.getContext().getUri().getBaseUri(), realm.getName())) });
            SignatureVerifierContext verifierContext = ((SignatureProvider)this.session.getProvider(SignatureProvider.class, verifier.getHeader().getAlgorithm().name())).verifier(verifier.getHeader().getKeyId());
            verifier.verifierContext(verifierContext);
            AccessToken accessToken = (AccessToken)verifier.verify().getToken();
            if (!this.tokenManager.checkTokenValidForIntrospection(this.session, realm, accessToken))
                throw new VerificationException("introspection_failed");
            return accessToken;
        } catch (ConfigurableTokenException e) {
            throw e;
        } catch (VerificationException|org.keycloak.OAuthErrorException e) {
            LOG.warn("Keycloak-ConfigurableToken: introspection of token failed", e);
            throw new ConfigurableTokenException("access_token_introspection_failed: " + e.getMessage());
        }
    }

    private String readAccessTokenFrom(HttpRequest request) throws ConfigurableTokenException {
        String authorization = request.getHttpHeaders().getHeaderString("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            LOG.warn("Keycloak-ConfigurableToken: no authorization header with bearer token");
            throw new ConfigurableTokenException("bearer_token_missing_in_authorization_header");
        }
        String token = authorization.substring(7);
        if (token == null || token.isEmpty()) {
            LOG.warn("Keycloak-ConfigurableToken: empty access token");
            throw new ConfigurableTokenException("missing_access_token");
        }
        return token;
    }

    private UserSessionModel findSession() throws ConfigurableTokenException {
        RealmModel realm = this.session.getContext().getRealm();
        System.out.println("realm==null");
        System.out.println(realm.getName());
        AuthenticationManager.AuthResult authenticated = (new AppAuthManager()).authenticateBearerToken(this.session, realm);
        if (authenticated == null) {
            LOG.warn("Keycloak-ConfigurableToken: user not authenticated");
            throw new ConfigurableTokenException("not_authenticated");
        }
//        if (authenticated.getToken().getRealmAccess() == null) {
//            LOG.warn("Keycloak-ConfigurableToken: no realm associated with authorization");
//            throw new ConfigurableTokenException("wrong_realm");
//        }
        UserModel user = authenticated.getUser();
        if (user == null || !user.isEnabled()) {
            LOG.warn("Keycloak-ConfigurableToken: user does not exist or is not enabled");
            throw new ConfigurableTokenException("invalid_user");
        }
        UserSessionModel userSession = authenticated.getSession();
        if (userSession == null) {
            LOG.warn("Keycloak-ConfigurableToken: user does not have any active session");
            throw new ConfigurableTokenException("missing_user_session");
        }
        return userSession;
    }

    private Response buildCorsResponse(@Context HttpRequest request, AccessTokenResponse response) {
        Cors cors = Cors.add(request).auth().allowedMethods(new String[] { "POST" }).auth().exposedHeaders(new String[] { "Access-Control-Allow-Methods", "Access-Control-Allow-Origin" }).allowAllOrigins();
        return cors.builder(Response.ok(response).type(MediaType.APPLICATION_JSON_TYPE)).build();
    }

    private AccessTokenResponse buildResponse(RealmModel realm, UserSessionModel userSession, ClientModel client, AuthenticatedClientSessionModel clientSession, AccessToken token) {
        EventBuilder eventBuilder = new EventBuilder(realm, this.session, this.session.getContext().getConnection());
        DefaultClientSessionContext defaultClientSessionContext = DefaultClientSessionContext.fromClientSessionScopeParameter(clientSession, this.session);
        return this.tokenManager.responseBuilder(realm, client, eventBuilder, this.session, userSession, (ClientSessionContext)defaultClientSessionContext)
                .accessToken(token)
                .build();
    }

    private void updateTokenExpiration(AccessToken token, TokenConfiguration tokenConfiguration, UserModel user) {
        Objects.requireNonNull(user);
        boolean longLivedTokenAllowed = ((Boolean)Optional.<RoleModel>ofNullable(this.session.getContext().getRealm().getRole(this.configuration.getLongLivedTokenRole())).map(user::hasRole).orElse(Boolean.valueOf(false))).booleanValue();
        token.expiration(tokenConfiguration.computeTokenExpiration(token.getExpiration(), longLivedTokenAllowed));
    }

    static class ConfigurableTokenException extends Exception {
        public ConfigurableTokenException(String message) {
            super(message);
        }
    }
}
