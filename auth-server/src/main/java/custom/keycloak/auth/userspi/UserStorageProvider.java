package custom.keycloak.auth.userspi;

import custom.keycloak.cache.TokenCacheRedisRepo;
import custom.keycloak.mapper.UserMapper;
import custom.keycloak.model.RoleGroup;
import custom.keycloak.model.User;
import custom.keycloak.service.UserService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Keycloak file based user storage provider
 */

@JBossLog
public class UserStorageProvider implements
        org.keycloak.storage.UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        UserQueryProvider,
        UserRegistrationProvider {
    private final KeycloakSession session;
    private final ComponentModel model; // represents how the provider is enabled and configured within a specific realm
    private UserService userService;
    private final UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private TokenCacheRedisRepo tokenCacheRedisRepo;
    private final Map<String, UserModel> loadedUsers;

    public UserStorageProvider(KeycloakSession session,
                               ComponentModel model,
                               UserService userService,
                               PasswordEncoder passwordEncoder,
                               TokenCacheRedisRepo tokenCacheRedisRepo) {
        this.session = session;
        this.model = model;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenCacheRedisRepo = tokenCacheRedisRepo;
        this.loadedUsers = new HashMap<>();
        ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
        WebApplicationContext appCtxt = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        this.userMapper = appCtxt.getBean(UserMapper.class);
    }

    /* UserLookupProvider interface implementation (Start) */
    @Override
    public void close() {
        userService.close();
        log.infov("End of transaction.");
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.infov("Looking up user via: id={0} realm={1}", id, realm.getId());
        StorageId storageId = new StorageId(id);
        String externalId = storageId.getExternalId(); // user id format - "f:" + component id + ":" + username
        return getUserByUsername(externalId, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        UserModel adapter = loadedUsers.get(username);
        if (adapter == null) {
//            session.userFederatedStorage().getStoredUsers(realm, 0, Integer.MAX_VALUE).stream().forEach(user -> {
//                log.infov("User in federated storage: {0}", user);
//                log.infov("Attributes: {0}", session.userFederatedStorage().getAttributes(realm, user));
//            });
//            User user = userService.getUserByUsername(username).orElseGet(null);
//            if (user != null) {
            if(username!=null)
                username = username.toLowerCase();
            User user = userService.getUserByUsernameMapper(username);

            if (user!=null && user.isActive()) {
                adapter = createAdapter(realm, user);
                adapter.setUsername(username);
                loadedUsers.put(username, adapter);
            }
        }
        return adapter;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.infov("Looking up user via email: email={0} realm={1}", email, realm.getId());
        return this.getUserByUsername(email, realm);
    }
    /* UserLookupProvider interface implementation (End) */

    /* CredentialInputValidator interface implementation (Start) */
    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        User userData = userService.getUserByUsername(user.getUsername()).orElseGet(null);
        String password = userData==null?null : userData.getPassword();
        log.infov("Checking authentication setup ...");
        return credentialType.equals(CredentialModel.PASSWORD) && password != null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(CredentialModel.PASSWORD);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;

        StorageId storageId = new StorageId(user.getId());
        String externalId = storageId.getExternalId();
        user.setUsername(externalId);

        UserCredentialModel cred = (UserCredentialModel) input;

        User userData = userService.getUserByUsername(user.getUsername()).orElseGet(null);
        String password = userData==null? null : userData.getPassword();

        if (password == null) return false;

        boolean isValidCredential = passwordEncoder.matches(cred.getValue(), password);
        if(isValidCredential){
            tokenCacheRedisRepo.save(
//                new TokenCache().builder()
//                        .username(user.getUsername())
//                        .authorities((List<String>) getAuthorities(user.getRoles()))
//                        .build()
                    user.getUsername(),
                    getAuthorities(userData.getRoles())
            );
        }
        return isValidCredential;
    }
    /* CredentialInputValidator interface implementation (End) */

    /* UserQueryProvider interface implementation (Start) */
    @Override
    public int getUsersCount(RealmModel realm) {
        log.infov("Getting user count ...");
        return userService.size();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        log.infov("Getting all users ...");
        return userService.findAll().stream()
                .map(x -> getUserByUsername(x.getUsername(), realm))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        return userService.findAll(firstResult, maxResults).stream()
                .map(x -> getUserByUsername(x.getUsername(), realm))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        log.infov("Searching for user: search={0} realm={1}", search, realm.getId());
        return userService.searchForUserByUsernameOrEmail(search).stream()
                .map(user -> getUserByUsername(user.getUsername(), realm))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return searchForUser(search, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        log.infov("Searching for user: params={0} realm={1}", params.toString(), realm.getId());
        if (params.isEmpty()) return getUsers(realm);
        String usernameParam = params.get("username");
        if (usernameParam == null) return Collections.EMPTY_LIST;
        return searchForUser(usernameParam, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        return searchForUser(params, realm);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return Collections.EMPTY_LIST;
    }

    /* UserQueryProvider interface implementation (End) */

    /* UserRegistrationProvider interface implementation (Start) */
    @Override
    public UserModel addUser(RealmModel realm, String username) {
        log.infov("Adding new user to file repository: username={0}", username);
        User user = new User();
        user.setUsername(username);
        userService.createUser(user);
        UserModel userModel = createAdapter(realm, user);
        return userModel;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        log.infov("Removing user: user={0}", user.getUsername());
        try {
//            userService.deleteUser(user.getUsername());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /* UserRegistrationProvider interface implementation (End) */

    private UserModel createAdapter(RealmModel realm, User user) {

        return new AbstractUserAdapterFederatedStorage(session, realm, model) { // anonymous class inheriting the abstract parent
            @Override
            public String getUsername() {
                log.infov("[Keycloak UserModel Adapter] Getting username ....");
                return user.getUsername();
            }

            @Override
            public String getEmail() {
                log.infov("[Keycloak UserModel Adapter] Getting email ....");
                return user.getEmail();
            }

            @Override
            public boolean isEnabled() {
                return user.isActive();
            }

            @Override
            public String getFirstName() {
                return user.getId().toString();
            }

            @Override
            public void setUsername(String username) {
                log.infov("[Keycloak UserModel Adapter] Setting username: {0}", username);
                user.setUsername(username);
            }

            @Override
            public void setEmail(String email) {
                log.infov("[Keycloak UserModel Adapter] Setting email: email={0}", email);
                user.setEmail(email);
            }

            @Override
            public Map<String, List<String>> getAttributes() {
                log.infov("[Keycloak UserModel Adapter] Getting all attributes ....");
                return getFederatedStorage().getAttributes(realm, this.getId());
            }

            @Override
            public void setAttribute(String name, List<String> values) {
                log.infov("[Keycloak UserModel Adapter] Setting attribute {0} with values {1}", name, values);

                getFederatedStorage().setAttribute(realm, this.getId(), "id", Arrays.asList(user.getId().toString()));
            }
        };
    }

    private final List<String> getAuthorities(final Collection<RoleGroup> roles) {
        List<String> grantedAuthorities = new ArrayList<>();
        if (roles != null) {
            boolean isSuperAdmin = roles.stream().anyMatch(roleGroup -> roleGroup.getKey().equals("SUPER_ADMIN"));
            if (!isSuperAdmin) {
                roles.forEach(role -> {
                    grantedAuthorities.add(role.getKey());
                    role.getRoleGroupScreenModulePrivilegeList().forEach(authorities -> {
                        grantedAuthorities.add(
                                new StringBuilder()
//                                        .append(authorities.getModule().getIndividualScreen().getScreenGroup().getName())
//                                        .append("-")
                                        .append(authorities.getModule().getIndividualScreen().getKey())
                                        .append("_")
                                        .append(authorities.getModule().getKey())
                                        .append("#")
                                        .append(authorities.getPrivilege().getKey()).toString()
                        );
                    });
                });
            } else {
                grantedAuthorities.add("SUPER_ADMIN");
            }
        }
        return grantedAuthorities;
    }
}
