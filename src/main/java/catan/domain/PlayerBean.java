package catan.domain;

public class PlayerBean {
    private String name;
    private String color;

    public PlayerBean() {
    }

    public PlayerBean(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
