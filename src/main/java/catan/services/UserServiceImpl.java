package catan.services;

import catan.dao.UserDao;
import catan.domain.UserBean;
import catan.domain.Session;
import catan.exception.UserException;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    UserDao userDao = new UserDao();


    @Override
    public String login(String username, String password) throws UserException {
        UserBean user = userDao.getUserByUsername(username);

        if(user == null){
            throw new UserException(
                    "No user found with username " + username,
                    "USERNAME_NOT_FOUND");
        }

        if(!password.equals(user.getPassword())){
            throw new UserException(
                    "Password '" + password + "' doesn't match to original password of user " + username,
                    "PASSWORD_INCORRECT");
        }

        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid, user);

        return session.getToken();
    }

    @Override
    public void logout(String username, String token) {

    }

    @Override
    public void register(String username, String password) throws UserException {
        UserBean user = userDao.getUserByUsername(username);
        if(user != null){
            throw new UserException(
                    "User " + username + " with such username already exists",
                    "USERNAME_ALREADY_EXISTS");
        }

        if(password == null || password.trim().length() == 0){
            throw new UserException(
                    "Password '" + password + "' is empty",
                    "PASSWORD_IS_EMPTY");
        }

        UserBean newUser = new UserBean();
        newUser.setUsername(username);
        newUser.setPassword(password);

        userDao.addNewUser(newUser);
    }
}
