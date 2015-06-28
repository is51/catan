package catan.dao;

import catan.domain.Session;
import catan.domain.UserBean;

import java.util.List;

public interface UserDatasource {

    void addUser(UserBean newUser);

    List<UserBean> getUsers();

    void addSession(Session newSession);

    List<Session> getSessions();

    void removeSessionByUsername(String token);

    void removeSessionByToken(String token);
}
