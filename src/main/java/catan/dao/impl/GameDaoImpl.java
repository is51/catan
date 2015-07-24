package catan.dao.impl;

import catan.dao.AbstractDao;
import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameStatus;
import catan.domain.model.game.GameUserBean;
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
    public void addNewGameUser(GameUserBean newGameUser) {
        persist(newGameUser);
    }

    @Override
    public GameBean getGameByGameId(int gameId) {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("gameId", gameId));

        return (GameBean) criteria.uniqueResult();
    }

    @Override
    public GameBean getGameByPrivateCode(String privateCode) {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("privateCode", privateCode));

        return (GameBean) criteria.uniqueResult();
    }

    @Override
    public List<GameBean> getGamesByCreatorId(int creatorId) {
        Query query = getSession().createQuery(
                "SELECT game " +
                "FROM " + GameBean.class.getSimpleName() + " AS game " +
                "WHERE game.creator.id in (" +
                    "SELECT user.id " +
                    "FROM " + UserBean.class.getName() + " AS user " +
                    "WHERE user.id = :creatorId)");
        query.setString("creatorId", String.valueOf(creatorId));

        //noinspection unchecked
        return (List<GameBean>) query.list();
    }

    @Override
    public List<GameBean> getGamesWithJoinedUser(int userId) {
        //TODO: change script to return appropriate values!
        Query query = getSession().createQuery(
                "SELECT game " +
                "FROM " + GameBean.class.getSimpleName() + " AS game " +
                    "LEFT JOIN game.gameUsers as gameUser " +
                "WHERE gameUser.user.id = :userId)");
        query.setString("userId", String.valueOf(userId));

        //noinspection unchecked
        return (List<GameBean>) query.list();
    }

    @Override
    public List<GameBean> getAllNewPublicGames() {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("privateGame", false));
        criteria.add(Restrictions.eq("status", GameStatus.NEW));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        //noinspection unchecked
        return (List<GameBean>) criteria.list();
    }

    @Override
    public List<String> getUsedActiveGamePrivateCodes() {
        Query query = getSession().createQuery(
                "SELECT game.privateCode " +
                        "FROM " + GameBean.class.getSimpleName() + " AS game " +
                        "WHERE game.status = '" + GameStatus.NEW.name() + "' AND game.privateGame = TRUE");


        //noinspection unchecked
        return (List<String>) query.list();
    }

    @Override
    public void updateGame(GameBean game) {
        update(game);
    }

    @Override
    public void updateGameUserBean(GameUserBean gameUserBean) {
        persist(gameUserBean);
    }

}
