package catan.domain.transfer.output;

public class UserDetails {
    private int id;
    private String username;
    private boolean guest;
    private String firstName;
    private String lastName;

    public UserDetails() {
    }

    public UserDetails(int id, String username, boolean guest) {
        this.id = id;
        this.username = username;
        this.guest = guest;
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

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
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
