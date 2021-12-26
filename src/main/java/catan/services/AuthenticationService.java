package catan.services;

import catan.domain.model.user.UserBean;
import catan.domain.exception.AuthenticationException;

public interface AuthenticationService {
    void authenticateAdminBySecretKey(String secretKey) throws AuthenticationException;

    UserBean authenticateUserByToken(String token) throws AuthenticationException;
}
