package catan.domain.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "USER_SESSION", uniqueConstraints = {
        @UniqueConstraint(columnNames = "TOKEN")})
public class UserSessionBean {

    @Id
    @Column(name = "TOKEN", unique = true, nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private UserBean user;

    public UserSessionBean() {
    }

    public UserSessionBean(String token, UserBean user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSessionBean)) return false;

        UserSessionBean that = (UserSessionBean) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserSessionBean{" +
                "token='" + token + '\'' +
                ", userId=" + (user == null ? "" : user.getId()) +
                '}';
    }
}
