package catan.services;

import catan.domain.model.user.UserBean;
import catan.exception.AuthenticationException;

public interface AuthenticationService {
    UserBean authenticateUserByToken(String token) throws AuthenticationException;
}
