package catan.services;

import catan.domain.model.user.UserBean;
import catan.exception.UserException;

public interface UserService {
    String login(String username, String password) throws UserException;

    void logout(String token) throws UserException;

    void register(String username, String password) throws UserException;

    UserBean getUserDetailsByToken(String token) throws UserException;
}
