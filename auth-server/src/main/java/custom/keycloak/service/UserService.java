package custom.keycloak.service;

import custom.keycloak.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();
    List<User> findAll(int start, int max);

    User getUserByUsernameMapper(String username);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> searchForUserByUsernameOrEmail(String searchString);
    List<User> searchForUserByUsernameOrEmail(String searchString, int start, int max);
    User getUserById(String id);
    User createUser(User user);
    void deleteUser(String username);
    User updateUser(User userEntity);
    int size();

    void close();

    void updatePassword(String encodePassword, String externalId);
}
