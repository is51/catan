package catan.dao;

import catan.domain.model.user.UserBean;
import catan.domain.model.user.UserSessionBean;

public interface UserDao {
    void addNewUser(UserBean newUser);

    void addNewSession(UserSessionBean newSession);

    UserBean getUserByUsername(String username);

    UserBean getUserByToken(String token);

    void removeSessionByUser(UserBean user);

    void removeSessionByToken(String token);
}
