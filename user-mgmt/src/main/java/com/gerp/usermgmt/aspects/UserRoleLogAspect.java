package com.gerp.usermgmt.aspects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gerp.shared.enums.RoleGroupEnum;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.annotations.UserRoleLogExecution;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.pojo.RoleLogDetailPojo;
import com.gerp.usermgmt.pojo.auth.UserAddPojo;
import com.gerp.usermgmt.pojo.auth.UserUpdatePojo;
import com.gerp.usermgmt.repo.RoleGroupRepo;
import com.gerp.usermgmt.repo.UserRoleLogRepo;
import com.gerp.usermgmt.repo.auth.UserRepo;
import com.gerp.usermgmt.services.usermgmt.UserRoleLogService;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import javassist.NotFoundException;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.util.*;

@Aspect
@Component
public class UserRoleLogAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserRoleLogRepo userRoleLogRepo;
    private final UserMapper userMapper;

    private final UserRepo  userRepo;

    private final UserRoleLogService userRoleLogService;
    private final RoleGroupRepo roleGroupRepo;

    private final RedisTemplate redisTemplate;

    private HashOperations hashOperations;

    public UserRoleLogAspect(UserRoleLogRepo userRoleLogRepo, UserMapper userMapper,
                             UserRepo userRepo,
                             UserRoleLogService userRoleLogService,
                             RoleGroupRepo roleGroupRepo,
                             RedisTemplate redisTemplate) {
        this.userRoleLogRepo = userRoleLogRepo;
        this.userMapper = userMapper;
        this.userRepo = userRepo;
        this.userRoleLogService = userRoleLogService;
        this.roleGroupRepo = roleGroupRepo;
        this. redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }



    @AfterReturning(pointcut = "@annotation(userRoleLogExecution) && args(data,bindObject)",
            returning = "response",
            argNames = "data,bindObject,response,userRoleLogExecution")
    public Object afterReturning(Object data, Object bindObject, Object response, UserRoleLogExecution userRoleLogExecution) throws JsonProcessingException {
        if(ObjectUtils.isEmpty(data)) {
            return null;
        }
        ObjectMapper oj = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter ow = oj.
                writer().withDefaultPrettyPrinter();
        Long userId= null;
        List<Long> roleIds= null;
        Boolean isNewRoleAdded= false;
        Object responseData  = ((GlobalApiResponse)(((ResponseEntity) response).getBody())).getData();
        if( responseData instanceof UserUpdatePojo) {
            UserUpdatePojo userUpdatePojo = (UserUpdatePojo) responseData;
            roleIds = userUpdatePojo.getRoleIds();
            userId = userUpdatePojo.getId();

        }
        if( responseData instanceof  Long){
            UserAddPojo userAddPojo = (UserAddPojo) data;
            roleIds = userAddPojo.getRoleIds();
            userId = (Long) responseData;
        }
        // removing the token cache from redis token cache
        hashOperations.delete("TOKEN", userId.toString());

        List<Long> previousUpdateRoles = userRoleLogService.findAllLatestChangedRoleIds(userId);
        if( previousUpdateRoles == null || !roleIds.equals(previousUpdateRoles) ){
            List<RoleLogDetailPojo> roleLogDetailPojos = userMapper.getRoleDetails(roleIds);
            UserRoleLog ur = UserRoleLog.builder().roleLogData(ow.writeValueAsString(roleLogDetailPojos))
                    .roleChangedUserId(userId)
                    .build();
            userRoleLogRepo.save(ur);

            // updating the admin role updated date on user column
            try {
                User user = userRepo.findById(userId).orElseThrow(RuntimeException::new);
                RoleGroup roleGroup = roleGroupRepo.findByKey(RoleGroupEnum.OFFICE_ADMINISTRATOR.toString()).orElseThrow(RuntimeException::new);
                if(roleIds.contains(roleGroup.getId()) || user.getAdminRoleUpdatedDate() ==null ||user.getAdminRoleCreatedDate() == null){

                    user.setAdminRoleUpdatedDate(user.getAdminRoleUpdatedDate() == null ? user.getLastModifiedDate(): new Timestamp(System.currentTimeMillis()));
                    if(user.getAdminRoleCreatedDate() == null) {
                        user.setAdminRoleCreatedDate( new Timestamp(System.currentTimeMillis()));
                    }
                    userRepo.save(user);
                }
            } catch (Exception ex){
                log.error("error updating admin role updated date ");
            }
        }



        return null;
    }

    List<RoleLogDetailPojo> parseJsonString(String data) throws JsonProcessingException {
        ObjectMapper o = new ObjectMapper();

        JsonNode jsonNode = o.readTree(data);
        if(jsonNode.isArray()) {

            return Arrays.asList(o.readValue(data, RoleLogDetailPojo[].class));
        } else {
            log.error("unable to parse json data for mapping");
            return Collections.emptyList();
        }
    }

}
