package catan.services;

import catan.domain.model.dashboard.CoordinatesBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class MapUtilTest {
    MapUtil mapUtil;
    private RandomValueGeneratorMock rvg;

    @Before
    public void setUp() {
        rvg = new RandomValueGeneratorMock();

        RandomUtil RandomUtil = new RandomUtil();
        RandomUtil.setRvg(rvg);

        mapUtil = new MapUtil();
        mapUtil.setRandomUtil(RandomUtil);
    }

    @After
    public void tearDown() {

    }

    //TODO:
    @Test
    public void generateNewRoundGameMap() {

    }

    //TODO:
    @Test
    public void createNode() {

    }

    //TODO:
    @Test
    public void getCurrentNodeOfLeftNeighbourHex() {

    }

    //TODO:
    @Test
    public void getCurrentNodeOfRightNeighbourHexReturnNullWhenNeighbourIsMissing() {
        //given
        GameBean game = new GameBean();  //TODO: populate game object
        CoordinatesBean currentHexCoordinates = new CoordinatesBean(0, -1);

        //when
        NodeBean rightNeighbour = mapUtil.getCurrentNodeOfRightNeighbourHex(game, NodePosition.UP, currentHexCoordinates);

        //then
        assertNull("Right neighbour of UP node that belongs to Hex with coordinates: (0, -1), should be null", rightNeighbour);
    }
}