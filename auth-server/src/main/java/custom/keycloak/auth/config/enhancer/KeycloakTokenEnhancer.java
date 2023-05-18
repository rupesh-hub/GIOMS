package custom.keycloak.auth.config.enhancer;

import com.google.auto.service.AutoService;
import custom.keycloak.cache.TokenCacheRedisRepo;
import custom.keycloak.mapper.UserMapper;
import custom.keycloak.model.RoleGroup;
import custom.keycloak.model.User;
import custom.keycloak.pojo.DelegatedPojo;
import custom.keycloak.service.UserService;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang.StringUtils;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ProtocolMapper;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.*;

@AutoService(ProtocolMapper.class)
@JBossLog
public class KeycloakTokenEnhancer extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "oidc-token-enhancer-mapper";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
    private String username;
    private DelegatedPojo delegatedPojo;
    private boolean isDelegated;


//    @PersistenceContext(unitName = "UserPU")
//    protected EntityManager entityManager;

    static {
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, KeycloakTokenEnhancer.class);
    }

    @Override
    public AccessToken transformAccessToken(AccessToken accessToken, ProtocolMapperModel protocolMapperModel, KeycloakSession keycloakSession, UserSessionModel userSessionModel, ClientSessionContext clientSessionContext) {
        log.debug("++++++++++++++++++++++++++++++++");
        ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
        WebApplicationContext appCtxt = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        UserService userService = appCtxt.getBean(UserService.class);
        UserMapper userMapper = appCtxt.getBean(UserMapper.class);

        getDelegatedUserName(userSessionModel,userMapper);

        Optional<User> userOptional = userService.getUserByUsername(username);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            accessToken.getOtherClaims().put("userId", user.getId());
            accessToken.getOtherClaims().put("preferred_username",userSessionModel.getLoginUsername().toLowerCase());
            accessToken.getOtherClaims().put("username", user.getUsername());
            accessToken.getOtherClaims().put("product_code", "PROD_GIOMS");
            List<String> roleKeys = userMapper.getRoles(user.getId());
            if(roleKeys!=null)
                accessToken.getOtherClaims().put("roles", StringUtils.join(roleKeys,","));

            if (isDelegated){
                TokenCacheRedisRepo tokenCacheRedisRepo = appCtxt.getBean(TokenCacheRedisRepo.class);
                tokenCacheRedisRepo.save(user.getUsername(), getAuthorities(user.getRoles()));
                accessToken.getOtherClaims().put("isDelegated",isDelegated);
                accessToken.getOtherClaims().put("delegatedId",delegatedPojo.getId());
                accessToken.getOtherClaims().put("expireDate",delegatedPojo.getExpireDate().toString());
                accessToken.getOtherClaims().put("effectiveDate",delegatedPojo.getEffectiveDate().toString());
            }
            String officeCode = user.getOfficeCode()==null?"0":user.getOfficeCode();
            String pisCode = user.getPisEmployeeCode()==null?"0":user.getPisEmployeeCode();
            Long organisationTypeId = userMapper.getEmployeeOfficeCategory(pisCode);
            accessToken.getOtherClaims().put("organizationTypeId", organisationTypeId);
            accessToken.getOtherClaims().put("officeCode", officeCode);
            accessToken.getOtherClaims().put("pisCode", pisCode);
            accessToken.getOtherClaims().put("sectionDesignationId", userMapper.employeeSectionDesignationId(pisCode, officeCode));
            accessToken.getOtherClaims().put("isOfficeHead", userMapper.checkIfOfficeHead(pisCode, officeCode) != 0);

            accessToken.getOtherClaims().put("setupCompleted", userMapper.checkSetupCompleted(pisCode));
        }
        log.debug("++++++++++++++++++++++++++++++++");
        return accessToken;
    }

    private void getDelegatedUserName(UserSessionModel userSessionModel, UserMapper userMapper){
        HttpServletRequestImpl request  = (HttpServletRequestImpl) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Map<String,String[]> data = request.getParameterMap();
        String[] delegate = data.get("pisCode");
        String userId = userSessionModel.getLoginUsername().toLowerCase();
        if (delegate != null && delegate[0] != null && !delegate[0].equals("null")){
            delegatedPojo = userMapper.checkIfDelegated(delegate[0],userId, LocalDateTime.now());
            if (delegatedPojo != null){
                username=delegate[0];
                isDelegated =true;
            }else {
                username = userId;
                isDelegated=false;
            };
        }else {
            username = userId;
            isDelegated= false;
        };
    }
    @Override
    public String getDisplayCategory() {
        return "Token Enhancer";
    }

    @Override
    public String getDisplayType() {
        return "Token Enhancer";
    }

    @Override
    public String getHelpText() {
        return "Add to claims for the User Service";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
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
    public static ProtocolMapperModel create(String name, boolean accessToken, boolean idToken, boolean userInfo) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<String, String>();
        if (accessToken) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        if (idToken) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        if (userInfo) config.put(OIDCAttributeMapperHelper.INCLUDE_IN_USERINFO, "true");
        mapper.setConfig(config);

        return mapper;
    }


}
