package catan.services.util.map;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.services.util.random.RandomValueGeneratorMock;
import catan.services.util.random.RandomValueProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;

public class MapUtilTest {
    MapUtil mapUtil;
    private RandomValueGeneratorMock rvg;

    @Before
    public void setUp() {
        rvg = new RandomValueGeneratorMock();

        RandomValueProvider RandomUtil = new RandomValueProvider();
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
        //TODO: populate game object
        Map<Coordinates, HexBean> tempCoordinatesToHexMap = new HashMap<Coordinates, HexBean>();
        Coordinates currentHexCoordinates = new Coordinates(0, -1);

        //when
        NodeBean rightNeighbour = mapUtil.getCurrentNodeOfRightNeighbourHex(tempCoordinatesToHexMap, NodePosition.TOP, currentHexCoordinates);

        //then
        assertNull("Right neighbour of TOP node that belongs to Hex with coordinates: (0, -1), should be null", rightNeighbour);
    }
}