package com.gerp.usermgmt.services.usermgmt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.enums.RoleLogFlagEnum;
import com.gerp.usermgmt.mapper.usermgmt.UserRoleLogMapper;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.model.fiscalyear.FiscalYear;
import com.gerp.usermgmt.pojo.RoleLogDetailPojo;
import com.gerp.usermgmt.pojo.RoleLogResponsePojo;
import com.gerp.usermgmt.repo.UserRoleLogRepo;
import com.gerp.usermgmt.repo.auth.UserRepo;
import com.gerp.usermgmt.repo.fiscalyear.FiscalYearRepo;
import com.gerp.usermgmt.services.usermgmt.UserRoleLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserRoleLogServiceImpl extends GenericServiceImpl<UserRoleLog, Long> implements UserRoleLogService {

    private final UserRoleLogRepo userRoleLogRepo;

    private final CustomMessageSource customMessageSource;

    private final UserRoleLogMapper userRoleLogMapper;

    private final FiscalYearRepo fiscalYearRepo;
    private final UserRepo userRepo;


    public UserRoleLogServiceImpl(UserRoleLogRepo userRoleLogRepo, CustomMessageSource customMessageSource, UserRoleLogMapper userRoleLogMapper,
                                  FiscalYearRepo fiscalYearRepo,
                                  UserRepo userRepo) {
        super(userRoleLogRepo);
        this.userRoleLogRepo = userRoleLogRepo;
        this.customMessageSource = customMessageSource;
        this.userRoleLogMapper = userRoleLogMapper;
        this.fiscalYearRepo = fiscalYearRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<RoleLogResponsePojo> getRoleHistory(String pisCode, String fiscalYearCode) {
        // finding all the id's of change history
        FiscalYear requestedFiscalYear = fiscalYearRepo.findByYearCode(fiscalYearCode);
        LocalDate startDate = requestedFiscalYear.getStartDate();
        LocalDate endDate = requestedFiscalYear.getEndDate();
        List<RoleLogResponsePojo> roleLogResponsePojos = userRoleLogMapper.findRoleHistory(pisCode, startDate, endDate);
        AtomicReference<Integer> count = new AtomicReference<>(0);
        List<RoleLogResponsePojo> finalRoleLogResponsePojos = roleLogResponsePojos;
        roleLogResponsePojos = roleLogResponsePojos.stream().map(roleLogResponsePojo -> {
            try {
                    count.getAndSet(count.get() + 1);
                    List<RoleLogDetailPojo> roleLogDetailPojos = parseJsonString(roleLogResponsePojo.getRoleLogJson());
                roleLogDetailPojos = addRoleLogFlag(roleLogDetailPojos, count, finalRoleLogResponsePojos);
                roleLogResponsePojo.setRoleLogJson(null);
                roleLogResponsePojo.setUpdatedRoles(roleLogDetailPojos);
                    return roleLogResponsePojo;

            } catch (JsonProcessingException e) {
                throw new CustomException("Error parsing log data into json ");
            }
        }
    ).collect(Collectors.toList());
        return  roleLogResponsePojos;
    }

    // this method set the flag of the change role of specific user
    public List<RoleLogDetailPojo> addRoleLogFlag(List<RoleLogDetailPojo> roleLogDetailPojos, AtomicReference<Integer> count, List<RoleLogResponsePojo> roleLogResponsePojos){
        Integer index = count.get();
        List<RoleLogDetailPojo> currentLogDetailPojos = roleLogDetailPojos;
        Set<RoleLogDetailPojo> newlist = new HashSet<>();
        if(index.equals(roleLogResponsePojos.size())){

            roleLogDetailPojos = roleLogDetailPojos.stream().map( roleLogDetailPojo -> {
                roleLogDetailPojo.setRoleLogFlagEnum(RoleLogFlagEnum.NOT_CHANGED);
                return roleLogDetailPojo;
            }).collect(Collectors.toList());

            return roleLogDetailPojos;

        } else {

            try {
                List<RoleLogDetailPojo> previousRoleLogs = parseJsonString(roleLogResponsePojos.get(index).getRoleLogJson());
                newlist.addAll(currentLogDetailPojos);
                newlist.addAll(previousRoleLogs);

                newlist = newlist.stream().map(roleLogDetailPojo -> {
                    if(previousRoleLogs.contains(roleLogDetailPojo) && currentLogDetailPojos.contains(roleLogDetailPojo)){
                        roleLogDetailPojo.setRoleLogFlagEnum(RoleLogFlagEnum.NOT_CHANGED);
                        return roleLogDetailPojo;
                    }
                    if(currentLogDetailPojos.contains(roleLogDetailPojo) && !previousRoleLogs.contains(roleLogDetailPojo)) {
                        roleLogDetailPojo.setRoleLogFlagEnum(RoleLogFlagEnum.ADD);
                        return roleLogDetailPojo;
                    }
                    roleLogDetailPojo.setRoleLogFlagEnum(RoleLogFlagEnum.REMOVED);
                    return roleLogDetailPojo;
                }).collect(Collectors.toSet());

            }catch (JsonProcessingException e){
                throw new CustomException("Error parsing log data into json ");
            }

        }
        return new ArrayList<>(newlist);

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



    @Override
    public List<Long> findAllLatestChangedRoleIds(Long userId) {
        UserRoleLog userRoleLogs = userRoleLogMapper.getLatestUserRoleLogByUserId(userId);
        if(userRoleLogs == null || userRoleLogs.getRoleLogData() == null) return null;
        List<Long> roleIds = new ArrayList<>();
        try {
            List<RoleLogDetailPojo> roleLogDetailPojos= parseJsonString(userRoleLogs.getRoleLogData());
            roleIds = roleLogDetailPojos.stream().map(roleLogDetailPojo -> roleLogDetailPojo.getId()).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return roleIds;
    }
}
