package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.enums.RoleType;
import com.gerp.usermgmt.mapper.usermgmt.RoleGroupMapper;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.pojo.auth.RolePojo;
import com.gerp.usermgmt.repo.RoleGroupRepo;
import com.gerp.usermgmt.services.usermgmt.RoleGroupService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleGroupServiceImpl extends GenericServiceImpl<RoleGroup, Long> implements RoleGroupService {

    private final RoleGroupRepo roleGroupRepo;
    private final CustomMessageSource customMessageSource;
    @Autowired private RoleGroupMapper roleGroupMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private TokenProcessorService tokenProcessorService;

    @Autowired
    public RoleGroupServiceImpl(RoleGroupRepo roleGroupRepo, CustomMessageSource customMessageSource) {
        super(roleGroupRepo);
        this.roleGroupRepo = roleGroupRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public RoleGroup create(RoleGroup newInstance) {
        newInstance.setOfficeCode(tokenProcessorService.getOfficeCode());
//        if(newInstance.getKey()==null || newInstance.getKey().trim().equals(""))
        newInstance.setKey(newInstance.getName().replaceAll(" ","").toUpperCase());
        if(tokenProcessorService.isAdmin())
            newInstance.setRoleType(RoleType.ADMIN_CREATE);
        return super.create(newInstance);
    }

    @Override
    public RoleGroup update(RoleGroup transientObject) {
        RoleGroup update = this.findById(transientObject.getId());

        this.validateRoleGroupUpdate(update);
        update.setDescription(transientObject.getDescription());
        update.setName(transientObject.getName());
        update.setNameNp(transientObject.getNameNp());

        return roleGroupRepo.save(update);
    }

    @Override
    public RoleGroup findRoleGroupByKey(String roleGroupKey) {
        Optional<RoleGroup> optionalRoleGroup = roleGroupRepo.findByKey(roleGroupKey);
        if (optionalRoleGroup.isPresent()) {
            return optionalRoleGroup.get();
        }
        throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("role.group")));
    }

    @Override
    public List<Long> findRoleGroupIdsByUsername(String username) {
        return roleGroupRepo.findRoleGroupByUsername(username);
    }

    @Override
    public List<RoleGroup> findRoleGroupListByUsername(String username) {
        if(tokenProcessorService.isAdmin()) {
          return  roleGroupRepo.findAllRoleList();
        }
        return roleGroupRepo.findRoleGroupListByUsername(username);
    }

    @Override
    public void validateRoleGroupUpdate(Long roleGroupId) {
        RoleGroup roleGroup = this.findById(roleGroupId);
        if(roleGroup!=null)
            this.validateRoleGroupUpdate(roleGroup);
    }

    @Override
    public void validateRoleGroupUpdate(RoleGroup update) {
        switch (update.getRoleType()){
            case SYSTEM:
                throw new RuntimeException("Can't Update");
            case ADMIN_CREATE:
                if(!tokenProcessorService.isAdmin())
                    throw new RuntimeException(customMessageSource.get("invalid.action"));
                break;
        }
    }

    @Override
    public List<RoleGroup> getAll() {
        List<RoleGroup> roleGroups = new ArrayList<>();
        if(tokenProcessorService.isAdmin())
            roleGroups = roleGroupMapper.findAllNonSystemForAdmin();
        else
            roleGroups = roleGroupMapper.findAllNonSystemAndByOfficeCode(tokenProcessorService.getOfficeCode());
        return roleGroups;
    }


    @Override
    public boolean checkExistingRoleGroupMapping(String pisCode, String roleKey) {
        RoleGroup roleGroup = this.findRoleGroupByKey(roleKey);
        Long userId = this.getUserIdByPisCode(pisCode);
        return roleGroupMapper.checkIfRoleMappingExists(userId, roleGroup.getId())==0?false:true;
    }

    @Override
    public void unAssignRole(String pisCode, String roleKey) {
        RoleGroup roleGroup = this.findRoleGroupByKey(roleKey);
        Long userId = this.getUserIdByPisCode(pisCode);
        roleGroupRepo.deleteUserRoleMappingByUserId(userId, roleGroup.getId());
    }

    @Override
    public void assignRole(String pisCode, String roleKey) {
        RoleGroup roleGroup = this.findRoleGroupByKey(roleKey);
        Long userId = this.getUserIdByPisCode(pisCode);
        roleGroupRepo.assignUserRoleMappingByUserId(userId, roleGroup.getId());
    }

    @Override
    public List<RolePojo> getRolesMinimalDetail() {
       return roleGroupRepo.findRoleMiniMalDetail();
    }

    private Long getUserIdByPisCode(String pisCode) {
        Long userId = userMapper.getUserIdByPisCode(pisCode);
        if(userId == null){
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("user")));
        }
        return userId;
    }

}




