package catan.services.util.play;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionParamsUtilTest {
    private GameUserBean gameUser;

    private  ActionParamsUtil actionParamsUtil;

    @Before
    public void setUp() {
        GameBean game = createGame();

        gameUser = new GameUserBean();
        gameUser.setGame(game);

        actionParamsUtil = new ActionParamsUtil();
    }

    @Test
    public void calculateMoveRobberParams() {
        assertEquals(4, gameUser.getGame().getHexes().size());
        List<Integer> hexIdsToMoveRobber = actionParamsUtil.calculateMoveRobberParams(gameUser);

        assertEquals(2, hexIdsToMoveRobber.size());
        assertTrue(hexIdsToMoveRobber.contains(1));
        assertTrue(hexIdsToMoveRobber.contains(2));
    }

    private GameBean createGame() {
        GameBean game = new GameBean();
        game.setStage(GameStage.MAIN);

        HexBean hexBean1 = new HexBean();
        hexBean1.setAbsoluteId(1);
        hexBean1.setResourceType(HexType.BRICK);
        hexBean1.setCoordinates(new Coordinates(1, 0));
        hexBean1.setGame(game);

        HexBean hexBean2 = new HexBean();
        hexBean2.setAbsoluteId(2);
        hexBean2.setResourceType(HexType.STONE);
        hexBean2.setCoordinates(new Coordinates(1, 1));
        hexBean2.setGame(game);

        HexBean robbedHex = new HexBean();
        robbedHex.setAbsoluteId(3);
        robbedHex.setRobbed(true);
        robbedHex.setResourceType(HexType.SHEEP);
        robbedHex.setCoordinates(new Coordinates(0, 1));
        robbedHex.setGame(game);

        HexBean emptyHex = new HexBean();
        emptyHex.setAbsoluteId(4);
        emptyHex.setResourceType(HexType.EMPTY);
        emptyHex.setCoordinates(new Coordinates(1, 1));
        emptyHex.setGame(game);

        Set<HexBean> hexes = new HashSet<HexBean>();
        hexes.add(hexBean1);
        hexes.add(hexBean2);
        hexes.add(robbedHex);
        hexes.add(emptyHex);

        game.setHexes(hexes);

        return game;
    }
}