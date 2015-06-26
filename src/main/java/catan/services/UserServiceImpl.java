package catan.services;

public class UserServiceImpl implements UserService {
    @Override
    public String login(String login, String password) {
        String userSessionToken = String.valueOf(Math.random() * 1000000);

        return login + userSessionToken + password;
    }

    @Override
    public void logout(String login, String token) {

    }

    @Override
    public void register(String login, String password) {

    }
}
