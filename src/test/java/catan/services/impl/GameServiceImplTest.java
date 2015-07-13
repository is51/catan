package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameStatus;
import catan.domain.model.user.UserBean;
import catan.exception.GameException;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        UserBean user = new UserBean(USER_NAME1, PASSWORD1);
        user.setId((int) System.currentTimeMillis());

        gameDao.addNewGame(anyObject(GameBean.class));
        expectLastCall().andAnswer(new IAnswer() {
            public Object answer() { // mock inner behavior of addNewGame method
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
        UserBean user = new UserBean(USER_NAME1, PASSWORD1);
        user.setId((int) System.currentTimeMillis());

        GameBean game1 = new GameBean(user, true, new Date(), GameStatus.NEW, 3, 4);
        game1.setGameId(1);

        GameBean game2 = new GameBean(user, false, new Date(), GameStatus.NEW, 3, 4);
        game2.setGameId(2);


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
        assertEquals(GameStatus.NEW, games.get(0).getStatus());

        assertNotNull(games.get(1));
        assertNotNull(games.get(1).getGameId());
        assertTrue(games.get(1).getGameId() > 0);
        assertNotNull(games.get(1).getCreator());
        assertEquals(user, games.get(1).getCreator());
        assertFalse(games.get(1).isPrivateGame());
        assertNotNull(games.get(1).getDateCreated());
        assertTrue(games.get(1).getDateCreated().getTime() > 0);
        assertTrue(games.get(1).getDateCreated().getTime() <= System.currentTimeMillis());
        assertEquals(GameStatus.NEW, games.get(1).getStatus());

        assertTrue(games.get(0).getGameId() != games.get(1).getGameId());
    }

    @Test
    public void getListOfAllPublicGamesSuccessful() throws GameException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1);
        user.setId((int) System.currentTimeMillis());

        GameBean game1 = new GameBean(user, false, new Date(), GameStatus.NEW, 3, 4);
        game1.setGameId(1);

        GameBean game2 = new GameBean(user, false, new Date(), GameStatus.NEW, 3, 4);
        game2.setGameId(2);

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
        assertEquals(GameStatus.NEW, games.get(0).getStatus());

        assertNotNull(games.get(1));
        assertNotNull(games.get(1).getGameId());
        assertTrue(games.get(1).getGameId() > 0);
        assertNotNull(games.get(1).getCreator());
        assertEquals(user, games.get(1).getCreator());
        assertFalse(games.get(1).isPrivateGame());
        assertNotNull(games.get(1).getDateCreated());
        assertTrue(games.get(1).getDateCreated().getTime() > 0);
        assertTrue(games.get(1).getDateCreated().getTime() <= System.currentTimeMillis());
        assertEquals(GameStatus.NEW, games.get(1).getStatus());

        assertTrue(games.get(0).getGameId() != games.get(1).getGameId());
    }
}