package catan.dao;

import catan.domain.UserBean;

import java.util.List;

public class UserDao {
    private UserDatasource datasource = new FileSystemUserDatasource();

    public UserBean getUserByUsername(String username) {
        List<UserBean> users = datasource.getUsers();
        for (UserBean user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public void addNewUser(UserBean newUser) {
        datasource.addUser(newUser);
    }
}
