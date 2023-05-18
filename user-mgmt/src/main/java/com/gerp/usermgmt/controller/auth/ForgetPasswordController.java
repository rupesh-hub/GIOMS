package com.gerp.usermgmt.controller.auth;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.utils.RandomGeneratorUtil;
import com.gerp.usermgmt.enums.TokenMessageType;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.model.auth.PasswordResetToken;
import com.gerp.usermgmt.pojo.forgetPassword.ForgetPasswordPojo;
import com.gerp.usermgmt.pojo.forgetPassword.PasswordResetPojo;
import com.gerp.usermgmt.pojo.forgetPassword.ResetStatusAndTokenPojo;
import com.gerp.usermgmt.services.auth.UserService;
import com.gerp.usermgmt.util.AESEncryptDecrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/forget-password")
public class ForgetPasswordController extends BaseController {


    private final UserService userService;
    private final RandomGeneratorUtil randomGeneratorUtil;
    private final AESEncryptDecrypt aesEncryptDecrypt;
    @Autowired private EmployeeMapper employeeMapper;

    public ForgetPasswordController(UserService userService, RandomGeneratorUtil randomGeneratorUtil, AESEncryptDecrypt aesEncryptDecrypt) {
        this.userService = userService;
        this.randomGeneratorUtil = randomGeneratorUtil;
        this.aesEncryptDecrypt = aesEncryptDecrypt;
    }

    @GetMapping(value = "/get-captcha")
    public ResponseEntity<?> getCaptcha() {
        String randomNumber = randomGeneratorUtil.getAlphaNumericString(5) +
                randomGeneratorUtil.getAlphaNumericString(7) +
                randomGeneratorUtil.getAlphaNumericString(5);
        StringBuilder stringBuilder = new StringBuilder(randomNumber);
        stringBuilder = stringBuilder.reverse();
        return ResponseEntity.ok(
                successResponse("Captcha code received",
                        randomGeneratorUtil.base64Encode(stringBuilder.toString())
                )
        );
    }

    @PostMapping(value = "/request")
    public ResponseEntity<?> initiateForgetPassword(@RequestBody ForgetPasswordPojo forgetPasswordPojo) {
        // if (validateCaptcha(forgetPasswordPojo.getEncryptedCaptcha(), forgetPasswordPojo.getCaptcha())) {

        Map<String, Object> data = userService.forgetPassword(forgetPasswordPojo);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("forget.password.success"),
                        data)
        );
       /* } else {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("invalid", customMessageSource.get("captcha")),
                            null)
            );
        }*/
    }

    @GetMapping(value = "/token/{tokenId}")
    public ResponseEntity<?> authorizeToken(@PathVariable String tokenId) {
        PasswordResetToken passwordResetToken = userService.getPasswordResetToken(tokenId);
        TokenMessageType tokenMessageType = userService.validateToken(passwordResetToken);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(tokenMessageType.name()),
                        tokenId
                )
        );
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> setNewPassword(@RequestBody PasswordResetPojo passwordResetPojo) {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        //PasswordResetToken passwordResetToken = userService.getPasswordResetToken(aesEncryptDecrypt.encrypt(passwordResetPojo.getToken()));
        PasswordResetToken passwordResetToken = userService.getPasswordResetToken(passwordResetPojo.getToken());
        TokenMessageType tokenMessageType = userService.validateToken(passwordResetToken);

        switch (tokenMessageType) {
            case SUCCESS:
                if (passwordResetPojo.getEmailOrUsername().equals(employeeMapper.getEmailAddress(passwordResetToken.getUser().getPisEmployeeCode())) ||
                        passwordResetPojo.getEmailOrUsername().equals(passwordResetToken.getUser().getUsername())) {
                    userService.updatePassword(passwordResetToken.getUser(), passwordResetPojo.getNewPassword());
                    globalApiResponse = successResponse(customMessageSource.get("password.update.success"), null);
                } else
                    globalApiResponse = errorResponse(customMessageSource.get("error.doesn't.match", "Email"), null);
                break;
            case INVALID:

            case EXPIRED:
                globalApiResponse = errorResponse(customMessageSource.get("error.expire", "Link"), null);
                break;
        }
        return ResponseEntity.ok(globalApiResponse);
    }


    /**
     * @param encryptedCaptcha
     * @param captchaValue     encrypted data is decoded from AESEncryptDecrypt
     * @return
     */
    private boolean validateCaptcha(String encryptedCaptcha, String captchaValue) {
        String decryptedData = randomGeneratorUtil.base64Decode(encryptedCaptcha);
        if (decryptedData == null)
            throw new RuntimeException(customMessageSource.get("invalid", customMessageSource.get("captcha")));
        StringBuilder stringBuilder = new StringBuilder(decryptedData);
        stringBuilder = stringBuilder.reverse();
        String actualData = stringBuilder.toString().substring(5, 12);
        if (actualData.equals(captchaValue)) {
            return true;
        }
        return false;
    }

    @PostMapping("/request-link")
    public ResponseEntity<?> generateTokenAndSendLink(@RequestBody ForgetPasswordPojo passwordResetPojo){
        Map<String, Object> data = userService.sendResetLinkInMail(passwordResetPojo);
        if(data != null){
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("forget.password"),
                            null
                    )
            );}
        else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("failed.sending"),
                            null
                    )
            );
        }
    }

    @PostMapping("/check-link-username")
    public ResponseEntity<?> checkStringQueryInUrl(@RequestBody ForgetPasswordPojo passwordResetPojo){
        ResetStatusAndTokenPojo data = userService.checkUsernameAndLink(passwordResetPojo);
        if(data != null){
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("forget.passwordReceived"),
                            data
                    )
            );
        }
        else {
            //return new ResponseEntity<String>(customMessageSource.get("forget.passwordLinkNotExist"),HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("forget.passwordLinkNotExist"),
                            null
                    )
            );
        }
    }

    @PostMapping("/process-link-username")
    public ResponseEntity<?> processStringQueryInUrl(@RequestBody ForgetPasswordPojo passwordResetPojo){
        Map<String, Object> data = userService.sendResetLinkInMail(passwordResetPojo);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("forget.password"),
                        data
                )
        );
    }

}
