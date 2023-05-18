package com.gerp.usermgmt.util;

import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.repo.auth.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationUtil {

    private final UserRepo userRepo;

    public AuthenticationUtil(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        return null;
    }
}
