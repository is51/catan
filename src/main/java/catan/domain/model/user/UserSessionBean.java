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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UserSessionBean))
            return false;
        UserSessionBean other = (UserSessionBean) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserBean [token=" + token + ", user=" + user + "]";
    }
}
