package catan.domain.model.game;

import catan.domain.model.user.UserBean;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Entity
@Table(name = "GAME_USER")
public class GameUserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int gameUserId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserBean user;

    @Column(name = "COLOR_ID", unique = false, nullable = false)
    private int colorId;

    @Column(name = "READY", nullable = false)
    private boolean ready;

    public GameUserBean() {
    }

    public GameUserBean(UserBean user, int colorId) {
        this.user = user;
        this.colorId = colorId;
    }

    public int getGameUserId() {
        return gameUserId;
    }

    public void setGameUserId(int gameUserId) {
        this.gameUserId = gameUserId;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameUserBean)) {
            return false;
        }
        final GameUserBean other = (GameUserBean) o;

        return new EqualsBuilder()
                .append(gameUserId, other.gameUserId)
                .append(user, other.user)
                .append(colorId, other.colorId)
                .append(ready, other.ready)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(gameUserId)
                .append(user)
                .append(colorId)
                .append(ready)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
