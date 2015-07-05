package catan.dao;

import catan.domain.model.UserBean;

public interface UserDao {
    void addNewUser(UserBean newUser);

    UserBean getUserByUsername(String username);

    UserBean getUserByToken(String token);

    void allocateNewTokenToUser(String token, UserBean user);

    void removeSession(String token);
}
