package catan.services;

import catan.exception.UserException;

public interface UserService{
    String login(String username, String password) throws UserException;
    void logout(String username, String token);
    void register(String username, String password) throws UserException;
}
