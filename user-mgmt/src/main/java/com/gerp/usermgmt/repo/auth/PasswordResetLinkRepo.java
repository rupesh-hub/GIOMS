package com.gerp.usermgmt.repo.auth;


import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.auth.PasswordResetLink;

public interface PasswordResetLinkRepo extends GenericRepository<PasswordResetLink, Long> {
    PasswordResetLink findByUser(User user);
    PasswordResetLink findByLinkToken(String link);
    void deleteByUser(User user);
}
