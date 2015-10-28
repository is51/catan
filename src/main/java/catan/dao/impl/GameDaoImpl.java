package catan.dao.impl;

import catan.dao.AbstractDao;
import catan.dao.GameDao;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStatus;
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

        GameBean game = (GameBean) criteria.uniqueResult();
        if(game == null){
            return null;
        }

        for (HexBean hex : game.getHexes()) {
            hex.getEdgeTopLeft().getHexes().setBottomRight(hex);
            hex.getEdgeTopRight().getHexes().setBottomLeft(hex);
            hex.getEdgeRight().getHexes().setLeft(hex);
            hex.getEdgeBottomRight().getHexes().setTopLeft(hex);
            hex.getEdgeBottomLeft().getHexes().setTopRight(hex);
            hex.getEdgeLeft().getHexes().setRight(hex);

            hex.getEdgeTopLeft().getNodes().setBottomLeft(hex.getNodeTopLeft());
            hex.getEdgeTopLeft().getNodes().setTopRight(hex.getNodeTop());
            hex.getEdgeTopRight().getNodes().setTopLeft(hex.getNodeTop());
            hex.getEdgeTopRight().getNodes().setBottomRight(hex.getNodeTopRight());
            hex.getEdgeRight().getNodes().setTop(hex.getNodeTopRight());
            hex.getEdgeRight().getNodes().setBottom(hex.getNodeBottomRight());
            hex.getEdgeBottomRight().getNodes().setTopRight(hex.getNodeBottomRight());
            hex.getEdgeBottomRight().getNodes().setBottomLeft(hex.getNodeBottom());
            hex.getEdgeBottomLeft().getNodes().setBottomRight(hex.getNodeBottom());
            hex.getEdgeBottomLeft().getNodes().setTopLeft(hex.getNodeBottomLeft());
            hex.getEdgeLeft().getNodes().setBottom(hex.getNodeBottomLeft());
            hex.getEdgeLeft().getNodes().setTop(hex.getNodeTopLeft());

            hex.getNodeTopLeft().getHexes().setBottomRight(hex);
            hex.getNodeTop().getHexes().setBottom(hex);
            hex.getNodeTopRight().getHexes().setBottomLeft(hex);
            hex.getNodeBottomRight().getHexes().setTopLeft(hex);
            hex.getNodeBottom().getHexes().setTop(hex);
            hex.getNodeBottomLeft().getHexes().setTopRight(hex);

            hex.getNodeTopLeft().getEdges().setBottom(hex.getEdgeLeft());
            hex.getNodeTopLeft().getEdges().setTopRight(hex.getEdgeTopLeft());
            hex.getNodeTop().getEdges().setBottomLeft(hex.getEdgeTopLeft());
            hex.getNodeTop().getEdges().setBottomRight(hex.getEdgeTopRight());
            hex.getNodeTopRight().getEdges().setTopLeft(hex.getEdgeTopRight());
            hex.getNodeTopRight().getEdges().setBottom(hex.getEdgeRight());
            hex.getNodeBottomRight().getEdges().setTop(hex.getEdgeRight());
            hex.getNodeBottomRight().getEdges().setBottomLeft(hex.getEdgeBottomRight());
            hex.getNodeBottom().getEdges().setTopRight(hex.getEdgeBottomRight());
            hex.getNodeBottom().getEdges().setTopLeft(hex.getEdgeBottomLeft());
            hex.getNodeBottomLeft().getEdges().setBottomRight(hex.getEdgeBottomLeft());
            hex.getNodeBottomLeft().getEdges().setTop(hex.getEdgeLeft());
        }

        return game;
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
    public void updateGameUser(GameUserBean gameUserBean) {
        persist(gameUserBean);
    }

}
