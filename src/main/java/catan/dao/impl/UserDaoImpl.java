package catan.dao.impl;

import catan.dao.AbstractDao;
import catan.dao.UserDao;
import catan.domain.model.user.UserBean;
import catan.domain.model.user.UserSessionBean;
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
        Criteria criteria = getSession().createCriteria(UserSessionBean.class);
        criteria.add(Restrictions.eq("token", token));

        UserSessionBean userSession = (UserSessionBean) criteria.uniqueResult();

        return userSession != null ? userSession.getUser() : null;
    }

    @Override
    public void allocateNewTokenToUser(String token, UserBean user) {
        Query query = getSession().createQuery("DELETE FROM " + UserSessionBean.class.getSimpleName() + " AS userSession WHERE userSession.id in " +
                "(SELECT userSession2.id FROM " + UserSessionBean.class.getName() + " AS userSession2 WHERE userSession2.user.username = :username)");
        query.setString("username", user.getUsername());
        query.executeUpdate();

        UserSessionBean userSession = new UserSessionBean(token, user);
        persist(userSession);
    }

    @Override
    public void removeSession(String token) {
        Query query = getSession().createQuery("DELETE FROM " + UserSessionBean.class.getSimpleName() + " AS userSession WHERE userSession.token = :token");
        query.setString("token", token);
        query.executeUpdate();
    }
}
