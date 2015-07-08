package catan.dao.impl;

import catan.dao.AbstractDao;
import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.user.UserBean;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("gameDao")
public class GameDaoImpl extends AbstractDao implements GameDao {

    @Override
    public void addNewGame(GameBean newGame) {
        persist(newGame);
    }

    @Override
    public GameBean getGameByGameId(int gameId) {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("gameId", gameId));

        return (GameBean) criteria.uniqueResult();
    }

    @Override
    public List<GameBean> getGamesByCreatorId(int creatorId) {
        Query query = getSession().createQuery("SELECT game FROM " + GameBean.class.getSimpleName() + " AS game WHERE game.creator.id in " +
                "(SELECT user.id FROM " + UserBean.class.getName() + " AS user WHERE user.id = :creatorId)");
        query.setString("creatorId", String.valueOf(creatorId));

        //noinspection unchecked
        return (List<GameBean>) query.list();
    }

    @Override
    public List<GameBean> getPublicGames() {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("privateGame", false));

        //noinspection unchecked
        return (List<GameBean>) criteria.list();
    }
}
