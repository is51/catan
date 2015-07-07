package catan.services;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.exception.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    private Logger log = LoggerFactory.getLogger(UserService.class);
    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String ERROR_CODE_TOKEN_INVALID = "TOKEN_INVALID";
    public static final String ERROR_CODE_INCORRECT_LOGIN_PASSWORD = "INCORRECT_LOGIN_PASSWORD";
    public static final String ERROR_CODE_USERNAME_ALREADY_EXISTS = "USERNAME_ALREADY_EXISTS";

    @Autowired
    UserDao userDao;

    @Override
    public String login(String username, String password) throws UserException {
        log.debug(">> Login user with username '" + username + "' ...");

        if (username == null || username.trim().length() == 0) {
            log.debug("<< Username is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        if (password == null || password.trim().length() == 0) {
            log.debug("<< Password is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        UserBean user = userDao.getUserByUsername(username);

        if (user == null) {
            log.debug("<< No user found with username '" + username + "'");
            throw new UserException(ERROR_CODE_INCORRECT_LOGIN_PASSWORD);
        }

        if (!user.getPassword().equals(password)) {
            log.debug("<< Password '" + password + "' doesn't match to original password of user '" + username + "'");
            throw new UserException(ERROR_CODE_INCORRECT_LOGIN_PASSWORD);
        }

        String token = UUID.randomUUID().toString();
        userDao.allocateNewTokenToUser(token, user);

        log.debug("<< User '" + username + "' successfully logged in and session '" + token + "' assigned to him");

        return token;
    }

    @Override
    public void logout(String token) {
        log.debug(">> Logout user with token '" + token + "' ...");
        userDao.removeSession(token);
        log.debug("<< User with token '" + token + "' successfully logged out");
    }

    @Override
    public void register(String username, String password) throws UserException {
        log.debug(">> Registering user with username '" + username + "'");

        if (username == null || username.trim().length() == 0) {
            log.debug("<< Username is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        if (password == null || password.trim().length() == 0) {
            log.debug("<< Password is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        UserBean user = userDao.getUserByUsername(username);
        if (user != null) {
            log.debug("<< User '" + username + "' with such username already exists");
            throw new UserException(ERROR_CODE_USERNAME_ALREADY_EXISTS);
        }

        UserBean newUser = new UserBean();
        newUser.setUsername(username);
        newUser.setPassword(password);

        userDao.addNewUser(newUser);
        log.debug("<< User '" + username + "' successfully registered");
    }

    @Override
    public UserBean getUserDetailsByToken(String token) throws UserException {
        log.debug(">> Search user with allocated token '" + token + "' ...");

        UserBean user = userDao.getUserByToken(token);
        if (user == null) {
            log.debug("<< User with allocated token '" + token + "' not found in system");
            throw new UserException(ERROR_CODE_TOKEN_INVALID);
        }

        log.debug("<< User '" + user.getUsername() + "' found with allocated token '" + token + "' , return details of this user");
        return user;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
