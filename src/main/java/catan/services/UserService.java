package catan.services;

public interface UserService{
    String login(String login, String password);
    void logout(String login, String token);
    void register(String login, String password);
}
