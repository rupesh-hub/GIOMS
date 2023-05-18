package com.gerp.usermgmt.services.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.json.ApiDetail;
import com.gerp.usermgmt.enums.TokenMessageType;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.auth.PasswordResetToken;
import com.gerp.usermgmt.pojo.auth.*;
import com.gerp.usermgmt.pojo.device.PisCodeToDeviceMapperPojo;
import com.gerp.usermgmt.pojo.forgetPassword.ForgetPasswordPojo;
import com.gerp.usermgmt.pojo.forgetPassword.ResetStatusAndTokenPojo;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface UserService extends GenericService<User, Long> {

    User createInternalUser(UserAddPojo userAddPojo);
    Boolean isUserAuthorized(ApiDetail userAddPojo);

    void updateInternalUser(UserUpdatePojo userUpdatePojo) throws JsonProcessingException;

    void updateInternalUserOffice(String pisCode, String officeCode);

    void updatePassword(PasswordPojo passwordUpdatePojo, Authentication authentication);

    void updatePassword(PasswordUpdatePojo passwordUpdatePojo);

    User getMBUser(String name);

    void updatePassword(User mbUser);

    String encodePassword(String password);

    boolean validatePassword(User mbUser, String oldPassword);

    void updatePassword(User user, String password);

    User save(User user);

    boolean checkPassword(Authentication auth);

    void updatePasswordOneTime(PasswordUpdateOneTimePojo passwordPojo, Authentication authentication);

    Map<String, Object> forgetPassword(ForgetPasswordPojo forgetPasswordPojo);

    PasswordResetToken getPasswordResetToken(String tokenId);

    TokenMessageType validateToken(PasswordResetToken passwordResetToken);

    Map<String, Object> sendResetLinkInMail(ForgetPasswordPojo forgetPasswordPojo);

    ResetStatusAndTokenPojo checkUsernameAndLink(ForgetPasswordPojo forgetPasswordPojo);

    com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserResponsePojo> filterData(GetRowsRequest paginatedRequest);

    UserResponsePojo getUserInfo();

    UserResponsePojo findByIdCustom(Long id);

    void toggleUserStatus(Long id);

    void saveDeviceId(PisCodeToDeviceMapperPojo pisCodeToDeviceMapperPojo);

    Long getDeviceIdByPisCode(String pisCode);

    boolean changePassword(PasswordChangePojo passwordChangePojo) throws JsonProcessingException;


}
