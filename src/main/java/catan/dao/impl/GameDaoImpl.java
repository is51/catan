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
    public void refreshGameBean(GameBean gameBean){
        getSession().refresh(gameBean);
    }

    @Override
    public void updateGame(GameBean game) {
        update(game);

        getSession().flush();
        getSession().clear();
    }

    @Override
    public void updateGameUser(GameUserBean gameUserBean) {
        persist(gameUserBean);

        getSession().flush();
        getSession().clear();
    }

    @Override
    public GameBean getGameByGameId(int gameId) {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("gameId", gameId));

        GameBean game = (GameBean) criteria.uniqueResult();

        return withMapElementsLinks(game);
    }


    @Override
    public GameBean getGameByPrivateCode(String privateCode) {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("privateCode", privateCode));

        GameBean game = (GameBean) criteria.uniqueResult();

        return withMapElementsLinks(game);
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
        query.setInteger("creatorId", creatorId);

        //TODO: mapElementsLinks are not populated
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
        query.setInteger("userId", userId);

        //TODO: mapElementsLinks are not populated
        //noinspection unchecked
        return (List<GameBean>) query.list();
    }

    @Override
    public List<GameBean> getAllNewPublicGames() {
        Criteria criteria = getSession().createCriteria(GameBean.class);
        criteria.add(Restrictions.eq("privateGame", false));
        criteria.add(Restrictions.eq("status", GameStatus.NEW));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        //TODO: mapElementsLinks are not populated
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

    private GameBean withMapElementsLinks(GameBean game) {
        if(game == null){
            return null;
        }

        for (HexBean hex : game.getHexes()) {
            hex.getEdges().getTopLeft().getHexes().setBottomRight(hex);
            hex.getEdges().getTopRight().getHexes().setBottomLeft(hex);
            hex.getEdges().getRight().getHexes().setLeft(hex);
            hex.getEdges().getBottomRight().getHexes().setTopLeft(hex);
            hex.getEdges().getBottomLeft().getHexes().setTopRight(hex);
            hex.getEdges().getLeft().getHexes().setRight(hex);

            hex.getEdges().getTopLeft().getNodes().setBottomLeft(hex.getNodes().getTopLeft());
            hex.getEdges().getTopLeft().getNodes().setTopRight(hex.getNodes().getTop());
            hex.getEdges().getTopRight().getNodes().setTopLeft(hex.getNodes().getTop());
            hex.getEdges().getTopRight().getNodes().setBottomRight(hex.getNodes().getTopRight());
            hex.getEdges().getRight().getNodes().setTop(hex.getNodes().getTopRight());
            hex.getEdges().getRight().getNodes().setBottom(hex.getNodes().getBottomRight());
            hex.getEdges().getBottomRight().getNodes().setTopRight(hex.getNodes().getBottomRight());
            hex.getEdges().getBottomRight().getNodes().setBottomLeft(hex.getNodes().getBottom());
            hex.getEdges().getBottomLeft().getNodes().setBottomRight(hex.getNodes().getBottom());
            hex.getEdges().getBottomLeft().getNodes().setTopLeft(hex.getNodes().getBottomLeft());
            hex.getEdges().getLeft().getNodes().setBottom(hex.getNodes().getBottomLeft());
            hex.getEdges().getLeft().getNodes().setTop(hex.getNodes().getTopLeft());

            hex.getNodes().getTopLeft().getHexes().setBottomRight(hex);
            hex.getNodes().getTop().getHexes().setBottom(hex);
            hex.getNodes().getTopRight().getHexes().setBottomLeft(hex);
            hex.getNodes().getBottomRight().getHexes().setTopLeft(hex);
            hex.getNodes().getBottom().getHexes().setTop(hex);
            hex.getNodes().getBottomLeft().getHexes().setTopRight(hex);

            hex.getNodes().getTopLeft().getEdges().setBottom(hex.getEdges().getLeft());
            hex.getNodes().getTopLeft().getEdges().setTopRight(hex.getEdges().getTopLeft());
            hex.getNodes().getTop().getEdges().setBottomLeft(hex.getEdges().getTopLeft());
            hex.getNodes().getTop().getEdges().setBottomRight(hex.getEdges().getTopRight());
            hex.getNodes().getTopRight().getEdges().setTopLeft(hex.getEdges().getTopRight());
            hex.getNodes().getTopRight().getEdges().setBottom(hex.getEdges().getRight());
            hex.getNodes().getBottomRight().getEdges().setTop(hex.getEdges().getRight());
            hex.getNodes().getBottomRight().getEdges().setBottomLeft(hex.getEdges().getBottomRight());
            hex.getNodes().getBottom().getEdges().setTopRight(hex.getEdges().getBottomRight());
            hex.getNodes().getBottom().getEdges().setTopLeft(hex.getEdges().getBottomLeft());
            hex.getNodes().getBottomLeft().getEdges().setBottomRight(hex.getEdges().getBottomLeft());
            hex.getNodes().getBottomLeft().getEdges().setTop(hex.getEdges().getLeft());
        }

        return game;
    }
}
