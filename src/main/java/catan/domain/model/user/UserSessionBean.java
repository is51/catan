package catan.domain.model.user;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

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
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
