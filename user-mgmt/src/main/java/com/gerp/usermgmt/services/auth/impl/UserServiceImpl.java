package com.gerp.usermgmt.services.auth.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.Mail;
import com.gerp.shared.pojo.json.ApiDetail;
import com.gerp.shared.utils.RandomGeneratorUtil;
import com.gerp.shared.utils.StringConstants;
import com.gerp.shared.utils.UtilityService;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.Proxy.CommunicationServiceData;
import com.gerp.usermgmt.cache.TokenCacheRedisRepo;
import com.gerp.usermgmt.config.DefaultPasswordEncoderFactories;
import com.gerp.usermgmt.enums.TokenMessageType;
import com.gerp.usermgmt.mapper.ModuleApiMappingMapper;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.auth.PasswordResetLink;
import com.gerp.usermgmt.model.auth.PasswordResetToken;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.office.OfficeHead;
import com.gerp.usermgmt.model.office.SectionHead;
import com.gerp.usermgmt.pojo.auth.*;
import com.gerp.usermgmt.pojo.delegation.TempDelegationResponsePojo;
import com.gerp.usermgmt.pojo.device.PisCodeToDeviceMapperPojo;
import com.gerp.usermgmt.pojo.forgetPassword.ForgetPasswordPojo;
import com.gerp.usermgmt.pojo.forgetPassword.ResetStatusAndTokenPojo;
import com.gerp.usermgmt.repo.ModuleRepo;
import com.gerp.usermgmt.repo.RoleGroupRepo;
import com.gerp.usermgmt.repo.auth.PasswordResetLinkRepo;
import com.gerp.usermgmt.repo.auth.PasswordResetTokenRepo;
import com.gerp.usermgmt.repo.auth.UserRepo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.repo.office.OfficeHeadRepo;
import com.gerp.usermgmt.repo.office.OfficeRepo;
import com.gerp.usermgmt.repo.office.SectionHeadRepo;
import com.gerp.usermgmt.services.auth.UserService;
import com.gerp.usermgmt.services.delegation.DelegationService;
import com.gerp.usermgmt.services.device.PisCodeDeviceIdMapperService;
import com.gerp.usermgmt.token.TokenProcessorService;
import com.gerp.usermgmt.util.AESEncryptDecrypt;
import com.gerp.usermgmt.util.AuthenticationUtil;
import org.modelmapper.ModelMapper;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements UserService {

    private final UserRepo userRepo;
    private final OfficeRepo officeRepo;
    private final RoleGroupRepo roleGroupRepo;
    private final OfficeHeadRepo officeHeadRepo;
    private final SectionHeadRepo sectionHeadRepo;
    private final CustomMessageSource customMessageSource;
    private final AuthenticationUtil authenticationUtil;
    private final RandomGeneratorUtil randomGeneratorUtil;
    private final DateConverter dateConverter = new DateConverter();
    private final UtilityService utilityService;
    private final ModuleRepo moduleRepo;
    private DateConverter converter = new DateConverter();
    private DelegationService delegationService;
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AESEncryptDecrypt aesEncryptDecrypt;
    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;
    @Autowired
    private PasswordResetLinkRepo passwordResetLinkRepo;
    @Autowired
    private CommunicationServiceData communicationServiceData;
    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private PisCodeDeviceIdMapperService pisCodeDeviceIdMapperService;

    @Autowired
    private ModuleApiMappingMapper moduleApiMappingMapper;

    public final TokenCacheRedisRepo tokenCacheRedisRepo;

    @Value("${link.front-reset-url}")
    private String frontIpForgetPasswordLink;

    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepo userRepo, OfficeRepo officeRepo, CustomMessageSource customMessageSource, RoleGroupRepo roleGroupRepo, RandomGeneratorUtil randomGeneratorUtil, UtilityService utilityService, AuthenticationUtil authenticationUtil, ModuleRepo moduleRepo, OfficeHeadRepo officeHeadRepo, SectionHeadRepo sectionHeadRepo, DelegationService delegationService, TokenCacheRedisRepo tokenCacheRedisRepo, ModelMapper modelMapper) {
        super(userRepo);
        this.userRepo = userRepo;
        this.officeRepo = officeRepo;
        this.customMessageSource = customMessageSource;
        this.roleGroupRepo = roleGroupRepo;
        this.randomGeneratorUtil = randomGeneratorUtil;
        this.utilityService = utilityService;
        this.authenticationUtil = authenticationUtil;
        this.moduleRepo = moduleRepo;
        this.officeHeadRepo = officeHeadRepo;
        this.sectionHeadRepo = sectionHeadRepo;
        this.delegationService = delegationService;
        this.tokenCacheRedisRepo = tokenCacheRedisRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public User createInternalUser(UserAddPojo userAddPojo) {
        if (userRepo.findByName(userAddPojo.getUsername()) != null)
            throw new RuntimeException(customMessageSource.get("error.already.exist", "User"));

        if (this.vaildatePisCode(userAddPojo.getPisEmployeeCode()))
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("employee")));

        Boolean isAdmin = tokenProcessorService.isAdmin();
        List<RoleGroup> roleList = new ArrayList<>();
        // not admin case
        if (!isAdmin) roleList = roleGroupRepo.findAllById(userAddPojo.getRoleIds());
            // admin case
        else {
            // get role data
            RoleGroup officeAdminRole = roleGroupRepo.findByKey(StringConstants.OFFICE_ADMINISTRATOR_ROLE).orElseThrow( () -> new CustomException("Office admin role not found"));
            // super admin can add organization admin role also added : 2023/01/10
            RoleGroup organizationAdminRole = roleGroupRepo.findByKey(StringConstants.ORGANISATION_ADMIN).orElseThrow( () -> new CustomException("Organization admin role not found"));
            int roleSize = userAddPojo.getRoleIds().size();
            // check role size got from api
//            if (roleSize > 1)
            if(roleSize > 2)
                // admin can only update specific role or office Admin
//                throw new RuntimeException(customMessageSource.get("can.only.add.office.admin"));
            throw new RuntimeException(customMessageSource.get("can.only.add.office.admin.and.organization.admin"));

            else if (roleSize ==1 || roleSize==2) {
                // verify if role id matches
//                if(!userAddPojo.getRoleIds().contains(roleGroup.getId()))
//                    throw new RuntimeException(customMessageSource.get("can.only.add.office.admin"));
//                else
//                roleList = Arrays.asList(roleGroup,organizationAdminRole);
                if(userAddPojo.getRoleIds().contains(officeAdminRole.getId())) roleList.add(officeAdminRole);
                if(userAddPojo.getRoleIds().contains(organizationAdminRole.getId())) roleList.add(organizationAdminRole);
            } else ;
        }

        String officeCode = employeeMapper.getOfficeCode(userAddPojo.getPisEmployeeCode());
        String tokenOfficeCode = tokenProcessorService.getOfficeCode();

        if (!tokenOfficeCode.equals(officeCode)) {
            if (!isAdmin) throw new RuntimeException(customMessageSource.get("invalid.action"));
        }

        // process role for office head or section head
        // add to office head table if provided role has office head
        // otherwise block it
        this.processRole(roleList, userAddPojo.getPisEmployeeCode(), officeCode);

        User user = new User().builder().username(userAddPojo.getUsername().trim().toLowerCase()).password(this.encodePassword(userAddPojo.getPassword())).roles(roleList).isPasswordChanged(false)
                //extra fields of user details field added in user
                .pisEmployeeCode(userAddPojo.getPisEmployeeCode()).officeCode(officeCode).build();

        if (userAddPojo.getEmail() != null && !userAddPojo.getEmail().trim().equals(""))
            employeeRepo.updateEmployeeEmail(userAddPojo.getEmail(), userAddPojo.getPisEmployeeCode());

        user.setActive(true);
        //needed to add users office information on the basis of user type
        return this.create(user);
    }

    @Override
    public Boolean isUserAuthorized(ApiDetail detail) {
        if(tokenProcessorService.isAdmin()) {
            return true;
        }
        return !Boolean.FALSE.equals(moduleApiMappingMapper.isUserAuthorized(
                detail.getName(), tokenProcessorService.getUserId(), detail.getMethod().toString().toUpperCase()));
    }

    @Override
    public void updateInternalUser(UserUpdatePojo userUpdatePojo) throws JsonProcessingException {
//        try {
            // todo format with standered format
        log.info("-----------ROLE_UPDATE_PROCESS_STARTS------------- \n");
                List<RoleGroup> roleList = roleGroupRepo.findAllById(userUpdatePojo.getRoleIds());

        String officeCode = employeeMapper.getOfficeCode(userUpdatePojo.getPisEmployeeCode());
        RoleGroup organizationAdminRole = roleGroupRepo.findByKey(StringConstants.ORGANISATION_ADMIN).orElseThrow( () -> new CustomException("Organization admin role not found"));


            // process role for office head or section head
        // add to office head table if provided role has office head
        // otherwise block it
        this.processRole(roleList, userUpdatePojo.getPisEmployeeCode(), officeCode);

        User user = this.findById(userUpdatePojo.getId());
        if (user == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("user")));

        Boolean isAdmin = tokenProcessorService.isAdmin();

        // not admin case

            if (!isAdmin) {
                // organization admin role cannot remove by other user rather than admin
                if(user.getRoles().contains(organizationAdminRole)) roleList.add(organizationAdminRole);
                user.getRoles().clear();
                user.getRoles().addAll(roleList);
            }

        // admin case
        else {
            // get role data
//            RoleGroup roleGroup = roleGroupRepo.findByKey(StringConstants.OFFICE_ADMINISTRATOR_ROLE).get();
                RoleGroup officeAdminRole = roleGroupRepo.findByKey(StringConstants.OFFICE_ADMINISTRATOR_ROLE).orElseThrow(() -> new CustomException("Office admin role not found"));
                user.getRoles().remove(officeAdminRole);
                user.getRoles().remove(organizationAdminRole);

                int roleSize = userUpdatePojo.getRoleIds().size();
            // check role size got from api
            if (roleSize > 2)
                // admin can only update specific role i.e office Admin
//                throw new RuntimeException(customMessageSource.get("can.only.add.office.admin"));
                throw new RuntimeException(customMessageSource.get("can.only.add.office.admin.and.organization.admin"));
            else if (roleSize == 2 || roleSize == 1) {
                // verify if role id matches
//                if(!userUpdatePojo.getRoleIds().contains(roleGroup.getId()))
//                    throw new RuntimeException(customMessageSource.get("can.only.add.office.admin"));
//                else {
//                user.getRoles().add(roleGroup);

                if(userUpdatePojo.getRoleIds().contains(officeAdminRole.getId()))
                    user.getRoles().add(officeAdminRole);
                if(userUpdatePojo.getRoleIds().contains(organizationAdminRole.getId()))
                    user.getRoles().add(organizationAdminRole);

//                }
            } else {
                // null case remove office admin role
//                user.getRoles().remove(roleGroup);
            }
        }
        // todo change to format specified

            log.info(
                "\n ROLE_UPDATE_OF_PISCODE="+ userUpdatePojo.getPisEmployeeCode() +
                        "\n ROLE_UPDATED_BY=" + tokenProcessorService.getPisCode() +
                        "\n DATA_FOR_UPDATE=" + new ObjectMapper().writeValueAsString(userUpdatePojo)
                + "\n PREVIOUS_ROLES=" + user.getRoles().stream().map(RoleGroup::getKey).collect(Collectors.toList())+
           "    \n NEW_ROLES=" + roleList.stream().map(RoleGroup::getKey).collect(Collectors.toList()));

        if (userUpdatePojo.getEmail() != null && !userUpdatePojo.getEmail().trim().equals(""))
            employeeRepo.updateEmployeeEmail(userUpdatePojo.getEmail(), user.getPisEmployeeCode());

        user.setLastModifiedBy(tokenProcessorService.getUserId());
        user.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));

        this.update(user);
        log.info("-----------ROLE_UPDATE_PROCESS_ENDS-------------");
//        } catch (RuntimeException ex){
//            throw new RuntimeException(customMessageSource.get("can.only.add.office.admin.and.organization.admin"));
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public void updateInternalUserOffice(String pisCode, String officeCode) {
        User user = userRepo.findByPisEmployeeCode(pisCode);
        Office o = officeRepo.findById(officeCode).orElseThrow(() -> new NullPointerException("no office found for provided code"));
        if (ObjectUtils.isEmpty(user)) {
            throw new RuntimeException("user not found of provided piscode");
        }
        user.setOfficeCode(officeCode);
        this.save(user);
    }


    private void processRole(List<RoleGroup> roleList, String employeePisCode, String officeCode) {
        log.info("update office head , section head role");
        roleList.forEach(x -> {
            if (x.getKey().equals(StringConstants.OFFICE_HEAD_ROLE)) {
                this.processOfficeHead(x.getId(), employeePisCode, officeCode);
            }
                if (x.getKey().equals(StringConstants.SECTION_HEAD_ROLE)) {
                //check if employee has section and process it
                boolean status = this.processSectionHead(x.getId(), employeePisCode, officeCode);
                if (!status) roleList.remove(x);
            }

        });
    }

    private void processOfficeHead(Long roleId, String employeePisCode, String officeCode) {
        OfficeHead update = officeHeadRepo.findByOfficeCode(officeCode);
        if (update == null) {
            OfficeHead officeHead = OfficeHead.builder().officeCode(officeCode).pisCode(employeePisCode).build();
            officeHeadRepo.save(officeHead);
        } else {
            List<Long> userIds = userMapper.getUserIdsByOfficeCode(officeCode, roleId);
            if (!userIds.isEmpty()) roleGroupRepo.deleteUserRoleMappingByUserIds(userIds, roleId);
            update.setPisCode(employeePisCode);
            officeHeadRepo.save(update);
        }
    }

    private boolean processSectionHead(Long roleId, String employeePisCode, String officeCode) {
        Long sectionCode = sectionHeadRepo.findSectionCodeByPisCodeAndOfficeCode(employeePisCode, officeCode);
        if (sectionCode == null) return false;
        SectionHead update = sectionHeadRepo.findBySectionCodeAndOfficeCode(sectionCode.toString(), officeCode);
        if (update == null) {
            SectionHead sectionHead = SectionHead.builder().sectionCode(sectionCode.toString()).officeCode(officeCode).pisCode(employeePisCode).build();
            sectionHeadRepo.save(sectionHead);
        } else {
            Long userId = userMapper.getUserIdByPisCode(update.getPisCode());
            if (userId != null) roleGroupRepo.deleteUserRoleMappingByUserId(userId, roleId);
            update.setPisCode(employeePisCode);
            sectionHeadRepo.save(update);
        }
        return true;
    }

    private boolean vaildatePisCode(String pisEmployeeCode) {
        return employeeMapper.checkPisCode(pisEmployeeCode) == 0 ? true : false;
    }

    @Override
    public void updatePassword(PasswordPojo passwordPojo, Authentication authentication) {
        User user = userRepo.findByName(authentication.getName());
        if (!this.validatePassword(user, passwordPojo.getOldPassword()))
            throw new RuntimeException(customMessageSource.get("password.notmatch"));
        if (!this.validatePasswordStrength(passwordPojo.getNewPassword()))
            throw new RuntimeException(customMessageSource.get("password.weak"));
        user.setPassword(this.encodePassword(passwordPojo.getNewPassword()));
        user.setIsPasswordChanged(true);
        this.updatePassword(user);
    }

    @Override
    public void updatePassword(PasswordUpdatePojo passwordUpdatePojo) {
        User user = this.findById(passwordUpdatePojo.getId());
        user.setPassword(this.encodePassword(passwordUpdatePojo.getNewPassword()));
        this.updatePassword(user);
    }

    @Override
    public User getMBUser(String name) {
        return userRepo.findByName(name);
    }

    @Override
    public void updatePassword(User mbUser) {
        userRepo.save(mbUser);
    }

    @Override
    public String encodePassword(String password) {
        return DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
    }

    @Override
    public boolean validatePassword(User mbUser, String oldPassword) {
        return DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder().matches(oldPassword, mbUser.getPassword());
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void updatePassword(User user, String password) {
        user.setPassword(this.encodePassword(password));
        this.updatePassword(user);
    }

    @Override
    public User save(User user) {
        User currentUser = userRepo.findById(user.getId()).get();
        return userRepo.save(currentUser);
    }

    @Override
    public void toggleUserStatus(Long id) {
        userRepo.deleteByUserId(id);
    }

    @Override
    public void delete(User persistentObject) {
//        repository.delete(persistentObject);
    }

    private boolean isSystemUser(User user) {
        if (user.getUsername().equalsIgnoreCase("Admin") || user.getUsername().equalsIgnoreCase("oaguser")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPassword(Authentication auth) {
        User user = userRepo.findByName(auth.getName());
        return user.getIsPasswordChanged();
    }

    @Override
    public void updatePasswordOneTime(PasswordUpdateOneTimePojo passwordPojo, Authentication auth) {
        User user = userRepo.findByName(auth.getName());
        if (user.getIsPasswordChanged()) throw new RuntimeException("Password Already Changed");
        user.setIsPasswordChanged(true);
        user.setPassword(this.encodePassword(passwordPojo.getNewPassword()));
        userRepo.save(user);
    }

    public Map<String, Object> forgetPassword(ForgetPasswordPojo forgetPasswordPojo) {
        User user = userRepo.findByUsernameOrEmail(forgetPasswordPojo.getEmailOrUsername());
        if (user == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("user")));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        String random = randomGeneratorUtil.generateRandomNumber(8);
        PasswordResetToken passwordResetToken = new PasswordResetToken().builder().token(aesEncryptDecrypt.encrypt(random)).expiryDate(cal.getTime()).user(user).build();
        PasswordResetToken passwordResetToken1 = passwordResetTokenRepo.findByUser(user);
        if (passwordResetToken1 != null) passwordResetToken.setId(passwordResetToken1.getId());
        passwordResetTokenRepo.save(passwordResetToken);

        Map<String, Object> data = new HashMap<>();
        data.put("name", forgetPasswordPojo.getEmailOrUsername());
        data.put("token", random);
        data.put("fullName", user.getUsername().toUpperCase());
        Mail<?> mail = new Mail<>().builder().subject("Forget Password").to(employeeMapper.getEmailAddress(user.getPisEmployeeCode())).template("forget-password").model(data).build();
        try {
//            communicationServiceData.sendEmailMessage(mail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(customMessageSource.get("error.cant.send", customMessageSource.get("email")));
        }

        return data;

    }

    public TokenMessageType validateToken(PasswordResetToken passwordResetToken) {
        if (passwordResetToken == null) return TokenMessageType.INVALID;
        if (convertToLocalDateViaInstant(passwordResetToken.getExpiryDate()).isBefore(LocalDate.now()))
            return TokenMessageType.EXPIRED;
        return TokenMessageType.SUCCESS;
    }

    public PasswordResetToken getPasswordResetToken(String tokenId) {
        return passwordResetTokenRepo.findByToken(tokenId);
    }

    public Map<String, Object> sendResetLinkInMail(ForgetPasswordPojo forgetPasswordPojo) {
        User user = userRepo.findByUsernameOrEmail(forgetPasswordPojo.getEmailOrUsername());
        if (user == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("user")));

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);

        String random = randomGeneratorUtil.generateRandomNumber(8);


        PasswordResetToken passwordResetToken = new PasswordResetToken().builder().token(aesEncryptDecrypt.encrypt(random)).expiryDate(cal.getTime()).user(user).build();

        PasswordResetToken passwordResetToken1 = passwordResetTokenRepo.findByUser(user);
        if (passwordResetToken1 != null) passwordResetToken.setId(passwordResetToken1.getId());
        passwordResetTokenRepo.save(passwordResetToken);
        String aliasName = UUID.randomUUID().toString() + randomGeneratorUtil.generateRandomNumber(8);
        String linkWithToken = frontIpForgetPasswordLink + aliasName;
        //String linkWithToken = "192.168.40.126:3400/forget-password?link="+aliasName;
        PasswordResetLink passwordResetLink = new PasswordResetLink().builder().linkToken(aliasName).user(user).expiryDate(cal.getTime()).tokenId(passwordResetToken).build();
        PasswordResetLink passwordResetLink1 = passwordResetLinkRepo.findByUser(user);
        if (passwordResetLink1 != null) {
            passwordResetLink.setId(passwordResetLink1.getId());
        }
        passwordResetLinkRepo.save(passwordResetLink);
        Map<String, Object> data = new HashMap<>();
        //data.put("name", forgetPasswordPojo.getEmailOrUsername());
        data.put("link", linkWithToken);
        //data.put("fullName", user.getUsername().toUpperCase());
        Mail<?> mail = new Mail<>().builder().subject("GIOMS Reset Password Link").to(employeeMapper.getEmailAddress(user.getPisEmployeeCode())).template("password-reset-link").model(data).build();
        try {
            communicationServiceData.sendEmailMessage(mail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(customMessageSource.get("error.cant.send", customMessageSource.get("email")));
        }

        return data;

    }

    @Override
    public ResetStatusAndTokenPojo checkUsernameAndLink(ForgetPasswordPojo forgetPasswordPojo) {
        //User user = userRepo.findByUsernameOrEmail(forgetPasswordPojo.getEmailOrUsername());
        //if(user != null){
        PasswordResetLink passwordResetLink = passwordResetLinkRepo.findByLinkToken(forgetPasswordPojo.getLink());
        if (passwordResetLink != null) {
            Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepo.findById(passwordResetLink.getTokenId().getId());
            if (passwordResetToken.isPresent())
                return ResetStatusAndTokenPojo.builder().isExist(true).token(passwordResetToken.get().getToken()).emailOrUsername(passwordResetToken.get().getUser().getUsername()).build();
        }
        //}
        return null;
    }

    @Override
    public UserResponsePojo getUserInfo() {
        setAuthorizationData();
        UserResponsePojo userResponsePojo = userMapper.getUserInfo(tokenProcessorService.getUserId());
        userResponsePojo.setIsOfficeHead(tokenProcessorService.getIsOfficeHead());
        if(tokenProcessorService.isDelegated() && tokenProcessorService.getDelegatedId() != null){
            TempDelegationResponsePojo tempDelegationResponsePojo = delegationService.getTemporaryDelegationById(tokenProcessorService.getDelegatedId());
            if(Boolean.TRUE.equals(tempDelegationResponsePojo.getIsReassignment())){
                userResponsePojo.setIsReassignment(Boolean.TRUE);
                userResponsePojo.setReassignmentSection(tempDelegationResponsePojo.getFromSection());
            }
            else {
                userResponsePojo.setIsDelegated(Boolean.TRUE);
            }
        }
        return userResponsePojo;
    }

    private void setAuthorizationData() {
        List<String> data = userMapper.getAccessPermissionData(tokenProcessorService.getUserId());
        tokenCacheRedisRepo.save(tokenProcessorService.getUserId().toString(), data);
    }

    @Override
    public UserResponsePojo findByIdCustom(Long id) {
        return userMapper.getUserInfo(id);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserResponsePojo> filterData(GetRowsRequest paginatedRequest) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserResponsePojo> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        // check if its for approver <value set from controller endpoint>
        if (paginatedRequest.getSearchField() == null) {
            paginatedRequest.setSearchField(new HashMap<>());
        }
        paginatedRequest.getSearchField().put("loggedInUser", tokenProcessorService.getUserId());
        if (!tokenProcessorService.isAdmin()) {
            paginatedRequest.getSearchField().put("officeCode", tokenProcessorService.getOfficeCode());
        }

        page = userMapper.filterData(page, paginatedRequest.getSearchField());
        // setting the roles for the employees
        setRoles(page);
        return page;
    }

    private void setRoles(Page<UserResponsePojo> page){
        page.setRecords(page.getRecords().stream().map(userResponsePojo -> {
            userResponsePojo.setRolePojos(userMapper.getAllByRolePojosByPisCode(userResponsePojo.getPisEmployeeCode()));
            return userResponsePojo;
        }).collect(Collectors.toList()));
    }

    @Override
    public void saveDeviceId(PisCodeToDeviceMapperPojo pisCodeToDeviceMapperPojo) {
        if (tokenProcessorService.isAdmin()) throw new RuntimeException(customMessageSource.get("invalid.action"));
        pisCodeToDeviceMapperPojo.setOfficeCode(tokenProcessorService.getOfficeCode());
        pisCodeDeviceIdMapperService.saveDeviceId(pisCodeToDeviceMapperPojo);
    }

    @Override
    public Long getDeviceIdByPisCode(String pisCode) {
        return pisCodeDeviceIdMapperService.getDeviceIdByPisCode(pisCode, tokenProcessorService.getOfficeCode());
    }

    /**
     * check same office admin with new and confirm password
     *
     * @param passwordChangePojo
     * @return
     */
    @Override
    public boolean changePassword(PasswordChangePojo passwordChangePojo) {
        log.info("PASSWORD_CHANGED_OF= "+ passwordChangePojo.getEmailOrUsername()+
                "\n PASSWORD_CHANGED_BY="+
                "USER_ID="+ tokenProcessorService.getUserId() + "PIS_CODE= "+ tokenProcessorService.getPisCode());
        if (!(
                        (tokenProcessorService.isOrganisationAdmin())
                        || (tokenProcessorService.isOfficeAdmin())
                        || (tokenProcessorService.getIsOfficeHead()
                        || (tokenProcessorService.isAdmin()))
        )
        ) {
            throw new RuntimeException(customMessageSource.get("user.unauthorized", null));
        }
        Long id = tokenProcessorService.getUserId();

        User adminUserOffice = userRepo.findById(id).orElseThrow(()-> new RuntimeException("User not found"));

        User userOffice = userRepo.findByPisEmployeeCode(passwordChangePojo.getEmailOrUsername().trim());
        if(userOffice==null){
            throw new RuntimeException(customMessageSource.get("office.not.found", null));
        }

        if ( (!tokenProcessorService.isAdmin() && !tokenProcessorService.isOrganisationAdmin()) && !adminUserOffice.getOfficeCode().equals(userOffice.getOfficeCode()))
            throw new RuntimeException(customMessageSource.get("same.office", null));

        if (!this.checkConfirmPassword(passwordChangePojo.getNewPassword(), passwordChangePojo.getConfirmPassword()))
            throw new RuntimeException(customMessageSource.get("password.mismatch", null));

        if (!this.validatePasswordStrength(passwordChangePojo.getNewPassword()))
            throw new RuntimeException(customMessageSource.get("password.weak", null));

        userOffice.setPassword(this.encodePassword(passwordChangePojo.getNewPassword()));
        userOffice.setIsPasswordChanged(false);
        this.updatePassword(userOffice);
        return true;
    }

    private boolean validatePasswordStrength(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(new LengthRule(8, 30), new UppercaseCharacterRule(1), new DigitCharacterRule(1), new SpecialCharacterRule(1),
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

    private boolean checkConfirmPassword(String newPassword, String confirmPassword) {
        return newPassword.equals(confirmPassword) ? true : false;
    }
}

