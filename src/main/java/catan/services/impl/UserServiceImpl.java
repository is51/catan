package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.domain.model.user.UserSessionBean;
import catan.domain.exception.UserException;
import catan.services.UserService;
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
    public static final String ERROR_CODE_INCORRECT_LOGIN_PASSWORD = "INCORRECT_LOGIN_PASSWORD";
    public static final String ERROR_CODE_USERNAME_ALREADY_EXISTS = "USERNAME_ALREADY_EXISTS";

    UserDao userDao;

    @Override
    public String loginUser(String username, String password) throws UserException {
        log.debug("Login user with username '" + username + "' ...");

        if (username == null || username.trim().length() == 0) {
            log.debug("Username is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        if (password == null || password.trim().length() == 0) {
            log.debug("Password is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        UserBean user = userDao.getUserByUsername(username);

        if (user == null) {
            log.debug("No user found with username '" + username + "'");
            throw new UserException(ERROR_CODE_INCORRECT_LOGIN_PASSWORD);
        }

        if(user.isGuest()){
            log.debug("Trying to login guest user '" + username + "' as permanent user");
            throw new UserException(ERROR_CODE_INCORRECT_LOGIN_PASSWORD);
        }

        if (!user.getPassword().equals(password)){
            log.debug("Password '" + password + "' doesn't match to original password of user '" + username + "'");
            throw new UserException(ERROR_CODE_INCORRECT_LOGIN_PASSWORD);
        }

        String token = allocateNewSessionTokenToUser(user);

        log.debug("User '" + username + "' successfully logged in and session '" + token + "' assigned to him");

        return token;
    }

    @Override
    public String loginGuest(String username) throws UserException {
        log.debug("Login guest user with username '" + username + "' ...");

        if (username == null || username.trim().length() == 0) {
            log.debug("Username is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        UserBean user = userDao.getUserByUsername(username);

        if (user == null) {
            log.debug("No user found with username '" + username + "'");
            throw new UserException(ERROR_CODE_INCORRECT_LOGIN_PASSWORD);
        }

        String token = allocateNewSessionTokenToUser(user);

        log.debug("Guest user '" + username + "' successfully logged in and session '" + token + "' assigned to him");

        return token;
    }

    @Override
    public void logout(String token) {
        log.debug("Logout user with token '" + token + "' ...");
        userDao.removeSessionByToken(token);
        log.debug("User with token '" + token + "' successfully logged out");
    }

    @Override
    public void registerUser(String username, String password) throws UserException {
        log.debug("Registering user with username '" + username + "'");

        if (username == null || username.trim().length() == 0) {
            log.debug("Username is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        if (password == null || password.trim().length() == 0) {
            log.debug("Password cannot be empty for permanent user");
            throw new UserException(ERROR_CODE_ERROR);
        }

        addNewUserIfNotExists(username, password, false);

        log.debug("User '" + username + "' successfully registered");
    }

    @Override
    public void registerGuest(String username) throws UserException {
        log.debug("Registering guest user with username '" + username + "'");

        if (username == null || username.trim().length() == 0) {
            log.debug("Username is empty");
            throw new UserException(ERROR_CODE_ERROR);
        }

        addNewUserIfNotExists(username, null, true);

        log.debug("Guest user '" + username + "' successfully registered");
    }

    private String allocateNewSessionTokenToUser(UserBean user) {
        userDao.removeSessionByUser(user);

        String token = UUID.randomUUID().toString();
        UserSessionBean userSession = new UserSessionBean(token, user);
        userDao.addNewSession(userSession);

        return token;
    }

    private void addNewUserIfNotExists(String username, String password, boolean guestUser) throws UserException {
        UserBean user = userDao.getUserByUsername(username);
        if (user != null) {
            log.debug("User '" + username + "' with such username already exists");
            throw new UserException(ERROR_CODE_USERNAME_ALREADY_EXISTS);
        }

        UserBean newUser = new UserBean(username, password, guestUser);

        userDao.addNewUser(newUser);
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
