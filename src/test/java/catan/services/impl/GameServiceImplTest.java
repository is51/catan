package catan.services.impl;

import catan.dao.GameDao;
import catan.dao.UserDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.user.UserBean;
import catan.exception.GameException;
import catan.exception.UserException;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class GameServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String PASSWORD1 = "12345";
    public static final String PASSWORD2 = "67890";

    GameDao gameDao;
    GameServiceImpl gameService;

    @Before
    public void setUp() {
        gameDao = createMock(GameDao.class);

        gameService = new GameServiceImpl();
        gameService.setGameDao(gameDao);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void createNewGameSuccessful() throws GameException {
        // GIVEN
        UserBean user = new UserBean();
        user.setId((int) System.currentTimeMillis());
        user.setUsername(USER_NAME1);
        user.setPassword(PASSWORD1);

        gameDao.addNewGame(anyObject(GameBean.class));
        expectLastCall().andAnswer(new IAnswer() {
            public Object answer() {
                GameBean arg1 = (GameBean) getCurrentArguments()[0];
                arg1.setGameId(1);

                return null;
            }
        });
        replay(gameDao);

        // WHEN
        GameBean game = gameService.createNewGame(user, true);

        // THEN
        assertNotNull(game);
        assertNotNull(game.getGameId());
        assertTrue(game.getGameId() > 0);
        assertNotNull(game.getCreator());
        assertEquals(user, game.getCreator());
        assertTrue(game.isPrivateGame());
        assertNotNull(game.getDateCreated());
        assertTrue(game.getDateCreated().getTime() > 0);
        assertTrue(game.getDateCreated().getTime() <= System.currentTimeMillis());
    }

    @Test
    public void getListOfGamesCreatedBySuccessful() throws GameException {
        // GIVEN
        UserBean user = new UserBean();
        user.setId((int) System.currentTimeMillis());
        user.setUsername(USER_NAME1);
        user.setPassword(PASSWORD1);

        GameBean game1 = new GameBean();
        game1.setGameId(1);
        game1.setCreator(user);
        game1.setDateCreated(new Date());
        game1.setPrivateGame(true);

        GameBean game2 = new GameBean();
        game2.setGameId(1);
        game2.setCreator(user);
        game2.setDateCreated(new Date());
        game2.setPrivateGame(false);

        List<GameBean> expectedGames = new ArrayList<GameBean>();
        expectedGames.add(game1);
        expectedGames.add(game2);

        expect(gameDao.getGamesByCreatorId(user.getId())).andStubReturn(expectedGames);
        replay(gameDao);

        // WHEN
        List<GameBean> games = gameService.getListOfGamesCreatedBy(user);

        // THEN
        assertNotNull(games);
        assertEquals(2, games.size());
        assertNotNull(games.get(0));
        assertNotNull(games.get(0).getGameId());
        assertTrue(games.get(0).getGameId() > 0);
        assertNotNull(games.get(0).getCreator());
        assertEquals(user, games.get(0).getCreator());
        assertTrue(games.get(0).isPrivateGame());
        assertNotNull(games.get(0).getDateCreated());
        assertTrue(games.get(0).getDateCreated().getTime() > 0);
        assertTrue(games.get(0).getDateCreated().getTime() <= System.currentTimeMillis());
        assertNotNull(games.get(1));
        assertNotNull(games.get(1).getGameId());
        assertTrue(games.get(1).getGameId() > 0);
        assertNotNull(games.get(1).getCreator());
        assertEquals(user, games.get(1).getCreator());
        assertFalse(games.get(1).isPrivateGame());
        assertNotNull(games.get(1).getDateCreated());
        assertTrue(games.get(1).getDateCreated().getTime() > 0);
        assertTrue(games.get(1).getDateCreated().getTime() <= System.currentTimeMillis());
    }

    @Test
    public void getListOfAllPublicGamesSuccessful() throws GameException {
        // GIVEN
        UserBean user = new UserBean();
        user.setId((int) System.currentTimeMillis());
        user.setUsername(USER_NAME1);
        user.setPassword(PASSWORD1);

        GameBean game1 = new GameBean();
        game1.setGameId(1);
        game1.setCreator(user);
        game1.setDateCreated(new Date());
        game1.setPrivateGame(false);

        GameBean game2 = new GameBean();
        game2.setGameId(1);
        game2.setCreator(user);
        game2.setDateCreated(new Date());
        game2.setPrivateGame(false);

        List<GameBean> expectedGames = new ArrayList<GameBean>();
        expectedGames.add(game1);
        expectedGames.add(game2);

        expect(gameDao.getPublicGames()).andStubReturn(expectedGames);
        replay(gameDao);

        // WHEN
        List<GameBean> games = gameService.getListOfAllPublicGames();

        // THEN
        assertNotNull(games);
        assertEquals(2, games.size());
        assertNotNull(games.get(0));
        assertNotNull(games.get(0).getGameId());
        assertTrue(games.get(0).getGameId() > 0);
        assertNotNull(games.get(0).getCreator());
        assertEquals(user, games.get(0).getCreator());
        assertFalse(games.get(0).isPrivateGame());
        assertNotNull(games.get(0).getDateCreated());
        assertTrue(games.get(0).getDateCreated().getTime() > 0);
        assertTrue(games.get(0).getDateCreated().getTime() <= System.currentTimeMillis());
        assertNotNull(games.get(1));
        assertNotNull(games.get(1).getGameId());
        assertTrue(games.get(1).getGameId() > 0);
        assertNotNull(games.get(1).getCreator());
        assertEquals(user, games.get(1).getCreator());
        assertFalse(games.get(1).isPrivateGame());
        assertNotNull(games.get(1).getDateCreated());
        assertTrue(games.get(1).getDateCreated().getTime() > 0);
        assertTrue(games.get(1).getDateCreated().getTime() <= System.currentTimeMillis());
    }
}