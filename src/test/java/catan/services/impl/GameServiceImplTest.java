package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.services.util.game.GameUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.map.MapUtil;
import catan.services.util.play.PreparationStageUtil;
import catan.services.util.random.RandomUtil;
import catan.domain.exception.GameException;
import catan.services.util.random.RandomValueGeneratorMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String USER_NAME2 = "userName2";
    public static final String PASSWORD1 = "12345";
    public static final String PASSWORD2 = "67890";

    public static final int DEFAULT_TARGET_VICTORY_POINTS = 12;
    public static final int DEFAULT_INITIAL_BUILDINGS_SET_ID = 1;

    @Mock
    private GameDao gameDao;
    @Mock
    private MapUtil mapUtil;
    @InjectMocks
    private GameServiceImpl gameService;
    @InjectMocks
    private GameUtil gameUtil;
    @InjectMocks
    private PlayUtil playUtil;
    @InjectMocks
    private PreparationStageUtil preparationStageUtil;

    private RandomValueGeneratorMock rvg = new RandomValueGeneratorMock();

    @Before
    public void setUp() {
        RandomUtil randomUtil = new RandomUtil();
        randomUtil.setRvg(rvg);

        gameUtil.setRandomUtil(randomUtil);
        gameUtil.setPlayUtil(playUtil);
        playUtil.setPreparationStageUtil(preparationStageUtil);

        gameService.setRandomUtil(randomUtil);
        gameService.setGameUtil(gameUtil);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void createNewPrivateGameSuccessful() throws GameException {
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

        ArgumentCaptor<GameBean> gameBeanArgumentCaptor = ArgumentCaptor.forClass(GameBean.class);

        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
        user.setId((int) System.currentTimeMillis());

        ArrayList<String> usedPrivateCodes = new ArrayList<String>();
        usedPrivateCodes.add("KP8428");

        when(gameDao.getUsedActiveGamePrivateCodes()).thenReturn(usedPrivateCodes);

        // WHEN
        GameBean game = gameService.createNewGame(user, true, Integer.toString(DEFAULT_TARGET_VICTORY_POINTS), Integer.toString(DEFAULT_INITIAL_BUILDINGS_SET_ID));

        // THEN
        verify(gameDao, times(1)).addNewGame(gameBeanArgumentCaptor.capture());
        verify(mapUtil, times(1)).generateNewRoundGameMap(game, GameServiceImpl.ROUND_MAP_SIZE);

        assertNotNull(game);
        assertNotNull(game.getGameId());
        assertEquals(game.getGameId(), gameBeanArgumentCaptor.getValue().getGameId());
        assertNotNull(game.getCreator());
        assertEquals(user, game.getCreator());
        assertTrue(game.isPrivateGame());
        assertNotNull(game.getDateCreated());
        assertTrue(game.getDateCreated().getTime() > 0);
        assertTrue(game.getDateCreated().getTime() <= System.currentTimeMillis());
        //Check that generated private code has reached limit of duplicates and increased number of digits
        assertEquals("FK84286", game.getPrivateCode());
        assertEquals(GameStatus.NEW, game.getStatus());
        assertEquals(GameServiceImpl.MIN_USERS, game.getMinPlayers());
        assertEquals(GameServiceImpl.MAX_USERS, game.getMaxPlayers());
        assertNotNull(game.getGameUsers());
        assertEquals(1, game.getGameUsers().size());
        assertEquals(user, game.getGameUsers().iterator().next().getUser());
        assertEquals(DEFAULT_TARGET_VICTORY_POINTS, game.getTargetVictoryPoints());
    }

    @Test
    public void createNewPublicGameFailedWhenUserIsNotRegistered() throws GameException {
        try {
            // GIVEN
            UserBean user = new UserBean(USER_NAME1, PASSWORD1, true);

            // WHEN
            GameBean game = gameService.createNewGame(user, false, "12", Integer.toString(DEFAULT_INITIAL_BUILDINGS_SET_ID));

            fail("GameException with error code '" + GameServiceImpl.GUEST_NOT_PERMITTED_ERROR + "' should be thrown, but returned game " + game);
        } catch (GameException e) {
            // THEN
            assertEquals(GameServiceImpl.GUEST_NOT_PERMITTED_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void getListOfGamesCreatedBySuccessful() throws GameException {
        // GIVEN
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);
        user.setId((int) System.currentTimeMillis());

        GameBean game1 = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 3, 4, DEFAULT_TARGET_VICTORY_POINTS, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game1.setGameId(1);

        GameBean game2 = new GameBean(user, new Date(), GameStatus.NEW, 3, 4, DEFAULT_TARGET_VICTORY_POINTS, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game2.setGameId(2);

        GameUserBean gameUser1 = new GameUserBean(user, 1, game1);
        GameUserBean gameUser2 = new GameUserBean(user, 1, game2);
        game1.getGameUsers().add(gameUser1);
        game2.getGameUsers().add(gameUser2);

        List<GameBean> expectedGames = new ArrayList<GameBean>();
        expectedGames.add(game1);
        expectedGames.add(game2);

        when(gameDao.getGamesWithJoinedUser(user.getId())).thenReturn(expectedGames);

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
        assertEquals(GameServiceImpl.MIN_USERS, games.get(0).getMinPlayers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(0).getMaxPlayers());
        assertNotNull(games.get(0).getGameUsers());
        assertEquals(1, games.get(0).getGameUsers().size());
        assertEquals(DEFAULT_TARGET_VICTORY_POINTS, games.get(0).getTargetVictoryPoints());
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
        assertEquals(GameServiceImpl.MIN_USERS, games.get(1).getMinPlayers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(1).getMaxPlayers());
        assertNotNull(games.get(1).getGameUsers());
        assertEquals(1, games.get(1).getGameUsers().size());
        assertEquals(DEFAULT_TARGET_VICTORY_POINTS, games.get(1).getTargetVictoryPoints());
        assertEquals(user, games.get(0).getGameUsers().iterator().next().getUser());

        assertTrue(games.get(0).getGameId() != games.get(1).getGameId());
    }

    @Test
    public void getListOfAllPublicGamesSuccessful() throws GameException {
        // GIVEN
        UserBean user1 = new UserBean(USER_NAME1, PASSWORD1, false);
        user1.setId((int) System.currentTimeMillis());

        UserBean user2 = new UserBean(USER_NAME2, PASSWORD2, false);
        user2.setId((int) System.currentTimeMillis());

        GameBean game1 = new GameBean(user1, new Date(), GameStatus.NEW, 3, 4, DEFAULT_TARGET_VICTORY_POINTS, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game1.setGameId(1);

        GameBean game2 = new GameBean(user2, new Date(), GameStatus.NEW, 3, 4, DEFAULT_TARGET_VICTORY_POINTS, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game2.setGameId(2);

        GameUserBean gameUser1 = new GameUserBean(user1, 1, game1);
        GameUserBean gameUser2 = new GameUserBean(user2, 1, game2);
        game1.getGameUsers().add(gameUser1);
        game2.getGameUsers().add(gameUser2);

        List<GameBean> expectedGames = new ArrayList<GameBean>();
        expectedGames.add(game1);
        expectedGames.add(game2);

        when(gameDao.getAllNewPublicGames()).thenReturn(expectedGames);

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
        assertEquals(GameServiceImpl.MIN_USERS, games.get(0).getMinPlayers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(0).getMaxPlayers());
        assertNotNull(games.get(0).getGameUsers());
        assertEquals(1, games.get(0).getGameUsers().size());
        assertEquals(DEFAULT_TARGET_VICTORY_POINTS, games.get(0).getTargetVictoryPoints());
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
        assertEquals(GameServiceImpl.MIN_USERS, games.get(1).getMinPlayers());
        assertEquals(GameServiceImpl.MAX_USERS, games.get(1).getMaxPlayers());
        assertNotNull(games.get(1).getGameUsers());
        assertEquals(1, games.get(1).getGameUsers().size());
        assertEquals(DEFAULT_TARGET_VICTORY_POINTS, games.get(1).getTargetVictoryPoints());
        assertEquals(user2, games.get(1).getGameUsers().iterator().next().getUser());

        assertTrue(games.get(0).getGameId() != games.get(1).getGameId());
    }

    @Test
    public void testSetReadyStatusSuccess() throws Exception {
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);

        GameBean game = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 3, 4, 12, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game.setGameId(1);
        game.getGameUsers().add(new GameUserBean(user, 1, game));

        ArgumentCaptor<GameUserBean> gameUserBeanCaptor = ArgumentCaptor.forClass(GameUserBean.class);

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        gameService.updateGameUserStatus(user, "1", true);

        verify(gameDao, times(1)).updateGameUser(gameUserBeanCaptor.capture());

        GameUserBean gameBean = gameUserBeanCaptor.getValue();
        assertEquals(gameBean.getUser(), user);
        assertEquals(gameBean.isReady(), true);
    }

    //TODO: add unit tests for join and leave games

    @Test
    public void testUnsetReadyStatusSuccess() throws Exception {
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);

        GameBean game = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 3, 4, 12, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game.setGameId(1);

        GameUserBean gameUserBean = new GameUserBean(user, 1, game);
        gameUserBean.setReady(true);
        game.getGameUsers().add(gameUserBean);

        ArgumentCaptor<GameUserBean> gameUserBeanCaptor = ArgumentCaptor.forClass(GameUserBean.class);

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        gameService.updateGameUserStatus(user, "1", false);

        verify(gameDao, times(1)).updateGameUser(gameUserBeanCaptor.capture());

        GameUserBean gameBean = gameUserBeanCaptor.getValue();
        assertEquals(gameBean.getUser(), user);
        assertEquals(gameBean.isReady(), false);
    }

    @Test(expected = GameException.class)
    public void testSetReadyStatus_UserHaventJoinedGame() throws Exception {
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);

        GameBean game = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 3, 4, DEFAULT_TARGET_VICTORY_POINTS, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game.setGameId(1);

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        gameService.updateGameUserStatus(user, "1", true);
    }

    @Test(expected = GameException.class)
    public void testSetReadyStatus_GameAlreadyStarted() throws Exception {
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);

        GameBean game = new GameBean(user, "TF3423", new Date(), GameStatus.PLAYING, 3, 4, DEFAULT_TARGET_VICTORY_POINTS, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game.setGameId(1);
        game.getGameUsers().add(new GameUserBean(user, 1, game));

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        gameService.updateGameUserStatus(user, "1", true);
    }

    @Test
    public void testSetReadyStatus_UserIsAlreadyReady() throws Exception {
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);


        GameBean game = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 3, 4, 12, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game.setGameId(1);
        GameUserBean gameUserBean = new GameUserBean(user, 1, game);
        game.getGameUsers().add(gameUserBean);
        game.getGameUsers().add(new GameUserBean(new UserBean("user1", "pwd", false), 2, game));

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        gameService.updateGameUserStatus(user, "1", true);
        gameService.updateGameUserStatus(user, "1", true);

        //There was only one call to updateGameUser
        verify(gameDao, times(1)).updateGameUser(any(GameUserBean.class));
        verify(gameDao, times(0)).updateGame(any(GameBean.class));
    }

    @Test
    public void testSetReadyStatus_AllPlayersAreReady_GameShouldBeStarted() throws Exception {
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);

        GameBean game = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 2, 4, 12, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game.setGameId(1);

        GameUserBean gameUserBean1 = new GameUserBean(new UserBean("user1", "pwd", false), 2, game);
        gameUserBean1.setReady(true);
        GameUserBean gameUserBean2 = new GameUserBean(user, 1, game);
        game.getGameUsers().add(gameUserBean1);
        game.getGameUsers().add(gameUserBean2);

        ArgumentCaptor<GameBean> gameBeanCaptor = ArgumentCaptor.forClass(GameBean.class);

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        gameService.updateGameUserStatus(user, "1", true);

        verify(gameDao, times(1)).updateGameUser(any(GameUserBean.class));
        verify(gameDao, times(1)).updateGame(gameBeanCaptor.capture());

        GameBean expectedGame = gameBeanCaptor.getValue();

        assertEquals(expectedGame.getCurrentMove(), new Integer(1));
        assertEquals(expectedGame.getStatus(), GameStatus.PLAYING);
        assertEquals(expectedGame.getStage(), GameStage.PREPARATION);
        assertEquals(expectedGame.getPreparationCycle(), new Integer(1));
        assertNotNull(expectedGame.getDateStarted());
        for(GameUserBean gameUser : expectedGame.getGameUsers()){
            assertTrue(gameUser.getMoveOrder() > 0);
        }
    }

    @Test
    public void testSetReadyStatus_ThereAreNotEnoughPlayers_GameShouldNotBeStarted() throws Exception {
        UserBean user = new UserBean(USER_NAME1, PASSWORD1, false);


        GameBean game = new GameBean(user, "TF3423", new Date(), GameStatus.NEW, 3, 4, 12, "[[CITY], [SETTLEMENT, SETTLEMENT], [ROAD, ROAD, ROAD]]");
        game.setGameId(1);

        GameUserBean gameUserBean1 = new GameUserBean(new UserBean("user1", "pwd", false), 2, game);
        gameUserBean1.setReady(true);
        GameUserBean gameUserBean2 = new GameUserBean(user, 1, game);
        game.getGameUsers().add(gameUserBean1);
        game.getGameUsers().add(gameUserBean2);

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        gameService.updateGameUserStatus(user, "1", true);

        verify(gameDao, times(1)).updateGameUser(any(GameUserBean.class));
        verify(gameDao, times(0)).updateGame(any(GameBean.class));
    }
}