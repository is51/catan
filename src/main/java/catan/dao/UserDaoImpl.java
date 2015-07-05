package catan.dao;

import catan.domain.model.UserBean;
import catan.domain.model.UserSession;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl extends AbstractDao implements UserDao {

    @Override
    public void addNewUser(UserBean newUser) {
        persist(newUser);
    }

    @Override
    public UserBean getUserByUsername(String username) {
        Criteria criteria = getSession().createCriteria(UserBean.class);
        criteria.add(Restrictions.eq("username", username));

        return (UserBean) criteria.uniqueResult();
    }

    @Override
    public UserBean getUserByToken(String token) {
        Criteria criteria = getSession().createCriteria(UserSession.class);
        criteria.add(Restrictions.eq("token", token));

        UserSession userSession = (UserSession) criteria.uniqueResult();

        return userSession != null ? userSession.getUser() : null;
    }

    @Override
    public void allocateNewTokenToUser(String token, UserBean user) {
        //TODO: think about make it transactional via 'delete' method
        Query query = getSession().createQuery("DELETE FROM UserSession AS userSession WHERE userSession.id in " +
                "(SELECT userSession2.id FROM UserSession AS userSession2 WHERE userSession2.user.username = :username)");
        query.setString("username", user.getUsername());
        query.executeUpdate();

        UserSession userSession = new UserSession();
        userSession.setToken(token);
        userSession.setUser(user);

        persist(userSession);
    }

    @Override
    public void removeSession(String token) {
        //TODO: think about make it transactional via 'delete' method
        Query query = getSession().createQuery("DELETE FROM UserSession AS userSession WHERE userSession.token = :token");
        query.setString("token", token);
        query.executeUpdate();
    }
}
