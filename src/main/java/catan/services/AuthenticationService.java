package catan.services;

import catan.domain.model.user.UserBean;
import catan.domain.exception.AuthenticationException;

public interface AuthenticationService {
    UserBean authenticateUserByToken(String token) throws AuthenticationException;
}
