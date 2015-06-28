package catan.dao;

import catan.domain.Session;
import catan.domain.UserBean;

import java.util.List;

public class UserDao {
    private UserDatasource datasource = new FileSystemUserDatasource();

    public void addNewUser(UserBean newUser) {
        datasource.addUser(newUser);
    }

    public UserBean getUserByUsername(String username) {
        List<UserBean> users = datasource.getUsers();
        for (UserBean user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public UserBean getUserByToken(String token) {
        List<Session> sessions = datasource.getSessions();
        for (Session session : sessions) {
            if (session.getToken().equals(token)) {
                return session.getUser();
            }
        }

        return null;
    }

    public void allocateNewTokenToUser(String token, UserBean user) {
        Session session = new Session(token, user);

        datasource.removeSessionByUsername(user.getUsername());
        datasource.addSession(session);
    }

    public void removeSession(String token){
        datasource.removeSessionByToken(token);
    }
}
