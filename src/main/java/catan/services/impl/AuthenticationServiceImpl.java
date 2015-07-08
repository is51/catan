package catan.services.impl;

import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.exception.AuthenticationException;
import catan.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authenticationService")
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    private Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    UserDao userDao;

    @Override
    public UserBean authenticateUserByToken(String token) throws AuthenticationException {
        log.debug(">> Search user with allocated token '" + token + "' ...");

        UserBean user = userDao.getUserByToken(token);
        if (user == null) {
            log.debug("<< User with allocated token '" + token + "' not found in system");
            throw new AuthenticationException();
        }

        log.debug("<< User '" + user.getUsername() + "' found with allocated token '" + token + "' , return details of this user");
        return user;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
