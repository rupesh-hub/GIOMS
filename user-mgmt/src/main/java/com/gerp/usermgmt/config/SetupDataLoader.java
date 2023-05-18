
package com.gerp.usermgmt.config;

import com.gerp.usermgmt.enums.RoleType;
import com.gerp.usermgmt.enums.SystemUnchangeableRole;
import com.gerp.usermgmt.model.Privilege;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.repo.RoleGroupRepo;
import com.gerp.usermgmt.repo.auth.PrivilegeRepo;
import com.gerp.usermgmt.repo.auth.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 *      Initial admin user, privilege and role setup
 * </p>
 */
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepo userRepository;

    private final PrivilegeRepo privilegeRepo;

    private final RoleGroupRepo roleGroupRepo;

    private List<String> PRIVILEGE = Arrays.asList(
            "Create",
            "Update",
            "Delete",
            "Read",
            "Approve",
            "Review"
    );

    @Autowired
    public SetupDataLoader(UserRepo userRepository,
                           PrivilegeRepo privilegeRepo,
                           RoleGroupRepo roleGroupRepo) {
        this.userRepository = userRepository;
        this.privilegeRepo = privilegeRepo;
        this.roleGroupRepo = roleGroupRepo;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        try {
            PRIVILEGE.forEach(x->{
                createPrivilegeIfNotFoundAll(x);
            });
            RoleGroup superAdmin = createRoleGroupIfNotFound(SystemUnchangeableRole.SUPER_ADMIN.toString());
            RoleGroup defaultRole = createRoleGroupIfNotFound(SystemUnchangeableRole.DEFAULT.toString());
            RoleGroup systemScreenSetupRole = createRoleGroupIfNotFound(SystemUnchangeableRole.SYSTEM_SCREEN_SETUP.toString());
            createUserIfNotFound("admin1", "Admin", "Admin", "admin123","00",
                    new ArrayList<RoleGroup>(Arrays.asList(superAdmin)), true);
            createUserIfNotFound("screen_setup", "Screen", "Setup", "Adm!n123!","00",
                    new ArrayList<RoleGroup>(Arrays.asList(systemScreenSetupRole)), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPrivilegeIfNotFoundAll(String s) {
        String key = s.toUpperCase();
        if (!privilegeRepo.findByKey(key).isPresent()) {
            Privilege privilege = new Privilege().builder()
                    .key(key)
                    .name(s)
                    .build();
            privilegeRepo.save(privilege);
        }
    }

    @Transactional
    public User createUserIfNotFound(final String username,
                                     final String firstName,
                                     final String lastName,
                                     final String password,
                                     final String officeCode,
                                     final Collection<RoleGroup> roleGroups,
                                     boolean enable) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));
            user.setRoles(roleGroups);
            user.setIsPasswordChanged(true);
            user.setActive(enable);
            user.setOfficeCode(officeCode);
            user = userRepository.save(user);
            return user;
        }
        return null;
    }

    private RoleGroup createRoleGroupIfNotFound(String roleGroupKey) {
        Optional<RoleGroup> roleGroupOptional = roleGroupRepo.findByKey(roleGroupKey);
        if (!roleGroupOptional.isPresent()) {
            RoleGroup roleGroup = new RoleGroup();
            roleGroup.setKey(roleGroupKey);
            roleGroup.setDescription("System created " + roleGroupKey);
            roleGroup.setRoleType(RoleType.SYSTEM);
            return roleGroupRepo.save(roleGroup);
        }else
            return roleGroupOptional.get();
    }

}

