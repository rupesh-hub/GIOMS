package com.gerp.usermgmt.component;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.RoleGroupEnum;
import com.gerp.shared.pojo.MultiReceiverNotificationPojo;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.usermgmt.enums.ModuleKeyEnum;
import com.gerp.usermgmt.enums.TransferAction;
import com.gerp.usermgmt.enums.TransferStatus;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferRequest;
import com.gerp.usermgmt.services.rabbitmq.RabbitMQService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class OrgTransferNotificationProducer {

    private final UserMapper userMapper;
    private final RabbitMQService notificationService;
    private final TokenProcessorService tokenProcessorService;
    private final EmployeeMapper employeeMapper;

    public OrgTransferNotificationProducer(UserMapper userMapper, RabbitMQService notificationService, TokenProcessorService tokenProcessorService, CustomMessageSource customMessageSource, EmployeeMapper employeeMapper) {
        this.userMapper = userMapper;
        this.notificationService = notificationService;
        this.tokenProcessorService = tokenProcessorService;
        this.employeeMapper = employeeMapper;
    }

    public void sendNotification(OrgTransferRequest orgTransferRequest, String action) {
        String detail;
        Set<String> userToNotify;
        Long moduleId =  orgTransferRequest.getId();
        TransferAction transferAction = TransferAction.valueOf(action);

        EmployeeMinimalPojo transferEmployee =  employeeMapper.getByCodeMinimal(orgTransferRequest.getEmployeePisCode());
        EmployeeMinimalPojo loggedInEmployee =  employeeMapper.getByCodeMinimal(tokenProcessorService.getPisCode());


        switch (transferAction) {
            case RQ:
                // notify to approval user for request
                // as well as the employee who is being transfer
                detail = "तपाईलाई "+ loggedInEmployee.getEmployeeNameEn() +"द्वारा "+ transferEmployee.getEmployeeNameNp() +"को सरुवा स्वीकृतिको लागि अनुरोध गरिएको छ ।";
//                this.notify(moduleId,getUserList(tokenProcessorService.getOfficeCode(),RoleGroupEnum.MAINTRANSFER), "सरुवा अनुरोध" ,detail );
                userToNotify = getAllUsersByModuleKeyAndOfficeCode(ModuleKeyEnum.APPROVED, tokenProcessorService.getOfficeCode());
                userToNotify.add(transferEmployee.getPisCode());
                  this.notify(moduleId, userToNotify, "सरुवा अनुरोध" ,detail );


                break;
            case R:
                // notify to requester that its rejected
                // as well as the employee being transfer
                detail = loggedInEmployee.getEmployeeNameNp() + "द्वारा "+ transferEmployee.getEmployeeNameNp() +"को सरुवा अनुरोध अस्वीकृत गरिएको छ ।";
//                this.notify(moduleId , getUserList(orgTransferRequest.getRequestedOffice().getCode(), RoleGroupEnum.REQUESTTRANSFER) , "सरुवा अस्वीकृत",detail);
                userToNotify = getAllUsersByModuleKeyAndOfficeCode(ModuleKeyEnum.REQUEST, tokenProcessorService.getOfficeCode());
                userToNotify.add(transferEmployee.getPisCode());
                this.notify(moduleId , userToNotify, "सरुवा अस्वीकृत",detail);

                break;
            case A:
                // notify requester that its approved
                detail = loggedInEmployee.getEmployeeNameNp() + "द्वारा " + transferEmployee.getEmployeeNameNp()  +"को सरुवा अनुरोध स्वीकृत गरिएको छ ।";
                this.notify(moduleId , getUserList(orgTransferRequest.getRequestedOffice().getCode(), RoleGroupEnum.REQUESTTRANSFER) , "सरुवा स्वीकृत",detail);


                //notify transferring employee that his transfer has been approved
                detail = loggedInEmployee.getEmployeeNameNp() +"द्वारा तपाइको सरुवा स्वीकृत गरिएको छ ।";
                this.notify(moduleId , transferEmployee.getPisCode(), "सरुवा स्वीकृत",detail);

                // notify current user office head that employee has departed as a transfer
                detail = loggedInEmployee.getEmployeeNameNp() + "द्वारा "+ transferEmployee.getEmployeeNameNp() +"को सरुवा अनुरोध स्वीकृत गरिएको छ ।";
                this.notify(moduleId ,  getUserList(tokenProcessorService.getOfficeCode(), RoleGroupEnum.OFFICE_HEAD)  , "सरुवा स्वीकृत",detail);

                //notify another office acknowledger
                detail = "तपाईलाई "+ orgTransferRequest.getRequestedOffice().getNameNp() +" बाट "+ transferEmployee.getEmployeeNameNp() +"को सरुवाको लागि जानकारी आएको छ ।";
                this.notify(moduleId , getUserList(orgTransferRequest.getTargetOffice().getCode(),RoleGroupEnum.MAINTRANSFER), "सरुवा स्वीकृत", detail);
                break;
            case AK:
                //notify user that his transfer has been acknowledged
                detail = "तपाईलाई "+ loggedInEmployee.getEmployeeNameNp() +"द्वारा "+ orgTransferRequest.getTargetOffice().getNameNp() + "मा सरुवा स्वीकृत गरिएको छ |";
                this.notify(moduleId , transferEmployee.getPisCode() , "सरुवा स्वीकृत",detail);


                // notify office head that user is acknowledged to its office
                detail = loggedInEmployee.getEmployeeNameNp() + "द्वारा "+ transferEmployee.getEmployeeNameNp() +"को सरुवा अनुरोध स्वीकृत गरिएको छ ।";
                this.notify(moduleId , getUserList(tokenProcessorService.getOfficeCode(), RoleGroupEnum.OFFICE_HEAD) , "सरुवा स्वीकृत",detail);
                break;
            default:
                break;
        }

    }

    public void notify(Long moduleId , Set<String> receiverCode , String subject , String detail) {
        notificationService.notificationProducer(MultiReceiverNotificationPojo.builder()
                .moduleId(moduleId)
                .module("USERMGMT")
                .sender(tokenProcessorService.getPisCode())
                .receiver(receiverCode)
                .subject(subject)
                .detail(detail)
                .pushNotification(true)
                .received(true)
                .build());
    }

    public void notify(Long moduleId , String receiverCode , String subject , String detail) {
        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId(moduleId)
                .module("USERMGMT")
                .sender(tokenProcessorService.getPisCode())
                .receiver(receiverCode)
                .subject(subject)
                .detail(detail)
                .pushNotification(true)
                .received(true)
                .build());
    }

    public void sendRequestNotification(String pisCode , OrgTransferRequest orgTransferRequest, String transferAction) {
        TransferAction action = TransferAction.valueOf(transferAction);
        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId(orgTransferRequest.getId())
                .module("USERMGMT")
                .sender(tokenProcessorService.getPisCode())
                .receiver(pisCode)
                .subject(this.getSubject(orgTransferRequest,action))
                .detail("तपाईलाई "+ orgTransferRequest.getCreatedBy() +"द्वारा"+orgTransferRequest.getEmployeePisCode() +"सरुवाको स्वीकृतिको लागि अनुरोध गरिएको छ ।")
                .pushNotification(true)
                .received(true)
                .build());
    }

    Set<String> getUserList(String officeCode, RoleGroupEnum roleGroupEnum) {
        List<String> pisCodeList;
        pisCodeList = userMapper.getEmployeePisCodeByRoleGroup(roleGroupEnum, officeCode);
        if(pisCodeList == null) {
            return new HashSet<>();
        }
       return new HashSet<>(pisCodeList);
    }

    String getSubject(OrgTransferRequest orgTransferRequest,TransferAction transferAction) {
        switch (transferAction) {
            case RQ:
                return "तपाईलाई "+ orgTransferRequest.getCreatedBy() +"द्वारा"+orgTransferRequest.getEmployeePisCode() +"सरुवाको स्वीकृतिको लागि अनुरोध गरिएको छ ।";
            case A:
                return "(कर्मचारीको नाम )द्वारा तपाईको सरुवा अनुरोध स्वीकृत गरिएको छ । (recipient office) बाट\n" +
                        " स्वीकृत भएपछि तपाइको कार्यालय स्वत: परिवर्तित हुनेछ ।";
            case C:
                return "Transfer Cancel";
            case R:
                return "Transfer Reject";
            case AK:
                return "Transfer Acknowledged";
            default:
                return "";
        }
    }

    String getDetail(TransferStatus transferStatus) {
//        customMessageSource.getNepali("delegation.message", getDateConvertedToNepaliDate(tempDelegationPojo.getEffectiveDate()),
//                getDateConvertedToNepaliDate(tempDelegationPojo.getExpireDate()),fromEmployeePojo.getNameNp())
        switch (transferStatus) {
            case A:
                return "Transfer has been Approved";
            case C:
                return "Transfer has been Cancelled";
            case R:
                return "Transfer has been REJECTED";
            case AK:
                return "Transfer has been Acknowledged";
            default:
                return "";
        }
    }

    Set<String> getAllUsersByModuleKeyAndOfficeCode(ModuleKeyEnum moduleKeyEnum, String officeCode) {
        List<String> pisCodeList = userMapper.getAllPisCodeByModule(officeCode, moduleKeyEnum);
        if(pisCodeList == null){
            return new HashSet<>();
        }
        return new HashSet<>(pisCodeList);
    }
}
