package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.exception.GameException;
import catan.services.RandomValeGeneratorMock;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GameServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String PASSWORD1 = "12345";
    public static final String PASSWORD2 = "67890";

    GameDao gameDao;
    GameServiceImpl gameService;
    private RandomValeGeneratorMock rvg;

    @Before
    public void setUp() {
        gameDao = createMock(GameDao.class);

        rvg = new RandomValeGeneratorMock();

        gameService = new GameServiceImpl();
        gameService.setGameDao(gameDao);
        gameService.getPrivateCodeUtil().setRvg(rvg);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void createNewGameSuccessful() throws GameException {
        // GIVEN

        //Should generate first privateCode KP8428
        rvg.setNextGeneratedValue(0.4);
        rvg.setNextGeneratedValue(0.6);
        rvg.setNextGeneratedValue(0.8254);
        //Should generate second privateCode KP8428
        rvg.setNextGeneratedValue(0.4);
        rvg.setNextGeneratedValue(0.6);
        rvg.setNextGeneratedValue(0.8254);
        //Should generate third privateCode KP8428
        rvg.setNextGeneratedValue(0.4);
        rvg.setNextGeneratedValue(0.6);
        rvg.setNextGeneratedValue(0.8254);
        //Should generate fourth privateCode KP8428
        rvg.setNextGeneratedValue(0.4);
        rvg.setNextGeneratedValue(0.6);
        rvg.setNextGeneratedValue(0.8254);
        //Should generate fifth privateCode KP8428
        rvg.setNextGeneratedValue(0.4);
        rvg.setNextGeneratedValue(0.6);
        rvg.setNextGeneratedValue(0.8254);

        //Should generate sixth privateCode FK84286
        rvg.setNextGeneratedValue(0.2);
        rvg.setNextGeneratedValue(0.4);
        rvg.setNextGeneratedValue(0.8254);

        UserBean user = new UserBean(USER_NAME1, PASSWORD1);
        user.setId((int) System.currentTimeMillis());

        ArrayList<String> usedPrivateCodes = new ArrayList<String>();
        usedPrivateCodes.add("KP8428");

        expect(gameDao.getUsedActiveGamePrivateCodes()).andStubReturn(usedPrivateCodes);
        gameDao.addNewGameUser(anyObject(GameUserBean.class));
        expectLastCall();

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
        GameBean game = gameService.createNewGame(user, true, "12");

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
        //Check that generated private code has reached limit of duplicates and increased number of digits
        assertEquals("FK84286", game.getPrivateCode());
        assertEquals(GameStatus.NEW, game.getStatus());
        assertEquals(GameServiceImpl.MIN_USERS, game.getMinUsers());
        assertEquals(GameServiceImpl.MAX_USERS, game.getMaxUsers());
        assertNotNull(game.getGameUsers());
        assertEquals(1, game.getGameUsers().size());
        assertEquals(user, game.getGameUsers().iterator().next().getUser());
        assertTrue(game.getTargetVictoryPoints() >= GameServiceImpl.MIN_VICTORY_POINTS);
    }

    @Test
    public void getListOfGamesCreatedBySuccessful() throws GameException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1);
        user.setId((int) System.currentTimeMillis());

        GameBean game1 = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 3, 4, 12);
        game1.setGameId(1);

        GameBean game2 = new GameBean(user, new Date(), GameStatus.NEW, 3, 4, 12);
        game2.setGameId(2);

        GameUserBean gameUser1 = new GameUserBean(user, 1, game1);
        GameUserBean gameUser2 = new GameUserBean(user, 1, game2);
        game1.getGameUsers().add(gameUser1);
        game2.getGameUsers().add(gameUser2);

        List<GameBean> expectedGames = new ArrayList<GameBean>();
        expectedGames.add(game1);
        expectedGames.add(game2);

        expect(gameDao.getGamesWithJoinedUser(user.getId())).andStubReturn(expectedGames);
        replay(gameDao);

        // WHEN
        List<GameBean> games = gameService.getListOfGamesWithJoinedUser(user);

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
        assertEquals("TF3423", games.get(0).getPrivateCode());
        assertEquals(GameServiceImpl.MIN_USERS, games.get(0).getMinUsers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(0).getMaxUsers());
        assertNotNull(games.get(0).getGameUsers());
        assertEquals(1, games.get(0).getGameUsers().size());
        assertTrue(games.get(0).getTargetVictoryPoints() >= GameServiceImpl.MIN_VICTORY_POINTS);
        assertEquals(user, games.get(0).getGameUsers().iterator().next().getUser());

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
        assertNull(games.get(1).getPrivateCode());
        assertEquals(GameServiceImpl.MIN_USERS, games.get(1).getMinUsers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(1).getMaxUsers());
        assertNotNull(games.get(1).getGameUsers());
        assertEquals(1, games.get(1).getGameUsers().size());
        assertTrue(games.get(1).getTargetVictoryPoints() >= GameServiceImpl.MIN_VICTORY_POINTS);
        assertEquals(user, games.get(0).getGameUsers().iterator().next().getUser());

        assertTrue(games.get(0).getGameId() != games.get(1).getGameId());
    }

    @Test
    public void getListOfAllPublicGamesSuccessful() throws GameException {
        // GIVEN
        UserBean user1 = new UserBean(USER_NAME1, PASSWORD1);
        user1.setId((int) System.currentTimeMillis());

        UserBean user2 = new UserBean(USER_NAME1, PASSWORD1);
        user2.setId((int) System.currentTimeMillis());

        GameBean game1 = new GameBean(user1, new Date(), GameStatus.NEW, 3, 4, 12);
        game1.setGameId(1);

        GameBean game2 = new GameBean(user2, new Date(), GameStatus.NEW, 3, 4, 12);
        game2.setGameId(2);

        GameUserBean gameUser1 = new GameUserBean(user1, 1, game1);
        GameUserBean gameUser2 = new GameUserBean(user2, 1, game2);
        game1.getGameUsers().add(gameUser1);
        game2.getGameUsers().add(gameUser2);

        List<GameBean> expectedGames = new ArrayList<GameBean>();
        expectedGames.add(game1);
        expectedGames.add(game2);

        expect(gameDao.getAllNewPublicGames()).andStubReturn(expectedGames);
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
        assertEquals(user1, games.get(0).getCreator());
        assertFalse(games.get(0).isPrivateGame());
        assertNotNull(games.get(0).getDateCreated());
        assertTrue(games.get(0).getDateCreated().getTime() > 0);
        assertTrue(games.get(0).getDateCreated().getTime() <= System.currentTimeMillis());
        assertEquals(GameStatus.NEW, games.get(0).getStatus());
        assertNull(games.get(0).getPrivateCode());
        assertEquals(GameServiceImpl.MIN_USERS, games.get(0).getMinUsers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(0).getMaxUsers());
        assertNotNull(games.get(0).getGameUsers());
        assertEquals(1, games.get(0).getGameUsers().size());
        assertTrue(games.get(0).getTargetVictoryPoints() >= GameServiceImpl.MIN_VICTORY_POINTS);
        assertEquals(user1, games.get(0).getGameUsers().iterator().next().getUser());

        assertNotNull(games.get(1));
        assertNotNull(games.get(1).getGameId());
        assertTrue(games.get(1).getGameId() > 0);
        assertNotNull(games.get(1).getCreator());
        assertEquals(user2, games.get(1).getCreator());
        assertFalse(games.get(1).isPrivateGame());
        assertNotNull(games.get(1).getDateCreated());
        assertTrue(games.get(1).getDateCreated().getTime() > 0);
        assertTrue(games.get(1).getDateCreated().getTime() <= System.currentTimeMillis());
        assertEquals(GameStatus.NEW, games.get(1).getStatus());
        assertNull(games.get(1).getPrivateCode());
        assertEquals(GameServiceImpl.MIN_USERS, games.get(1).getMinUsers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(1).getMaxUsers());
        assertNotNull(games.get(1).getGameUsers());
        assertEquals(1, games.get(1).getGameUsers().size());
        assertTrue(games.get(1).getTargetVictoryPoints() >= GameServiceImpl.MIN_VICTORY_POINTS);

        assertEquals(user2, games.get(0).getGameUsers().iterator().next().getUser());

        assertTrue(games.get(0).getGameId() != games.get(1).getGameId());
    }
}