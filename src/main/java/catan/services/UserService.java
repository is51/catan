package catan.services;

import catan.exception.UserException;

public interface UserService {
    String loginUser(String username, String password) throws UserException;

    String loginGuest(String username) throws UserException;

    void logout(String token) throws UserException;

    void registerUser(String username, String password) throws UserException;

    void registerGuest(String username) throws UserException;
}
