package catan.domain.transfer.output;

import catan.domain.model.user.UserBean;

//TODO: move to catan.domain.transfer.output.user after pull request
public class UserDetails {
    private int id;
    private String username;
    private String firstName;
    private String lastName;

    public UserDetails() {
    }

    public UserDetails(UserBean user) {
        this.id = user.getId();
        this.username = user.getUsername();
        //TODO: should get firstName and lastName from UserBean in future
        this.firstName = "vasya";
        this.lastName = "pupkin";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
