package catan.dao;

import catan.domain.UserBean;

import java.util.List;

public interface UserDatasource {
    void addUser(UserBean newUser);

    List<UserBean> getUsers();
}
