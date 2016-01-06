package catan.services.util.play;

import catan.domain.model.dashboard.Building;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.VerticalLinks;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.user.UserBean;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by oborkovskyi on 12/25/2015.
 */
public class MainStageUtilTest {

    private MainStageUtil mainStageUtil = new MainStageUtil();

    @Test
    public void shouldSkipRobbedHexWhenProducingResources() throws Exception {
        GameUserBean buildingOwner = new GameUserBean();

        ArrayList<HexBean> hexes = prepareDiceHexes(buildingOwner);

        mainStageUtil.produceResourcesFromActiveDiceHexes(hexes);

        assertEquals(buildingOwner.getResources().getBrick(), 1);
        assertEquals(buildingOwner.getResources().getSheep(), 0);
        assertEquals(buildingOwner.getResources().getStone(), 0);
    }

    private ArrayList<HexBean> prepareDiceHexes(GameUserBean buildingOwner) {
        Resources resources = new Resources();

        UserBean user = new UserBean();
        user.setUsername("Suchka");

        buildingOwner.setResources(resources);
        buildingOwner.setUser(user);

        Building<NodeBuiltType> building = new Building<NodeBuiltType>();
        building.setBuilt(NodeBuiltType.SETTLEMENT);
        building.setBuildingOwner(buildingOwner);

        NodeBean top = new NodeBean();
        top.setBuilding(building);

        VerticalLinks<NodeBean> nodes = new VerticalLinks<NodeBean>();
        nodes.setTop(top);
        nodes.setBottom(new NodeBean());

        HexBean hexBean1 = new HexBean();
        hexBean1.setNodes(nodes);
        hexBean1.setResourceType(HexType.BRICK);

        HexBean hexBean2 = new HexBean();
        hexBean2.setNodes(nodes);
        hexBean2.setResourceType(HexType.SHEEP);
        hexBean2.setRobbed(true);

        HexBean hexBean3 = new HexBean();
        hexBean3.setResourceType(HexType.STONE);

        ArrayList<HexBean> hexes = new ArrayList<HexBean>();
        hexes.add(hexBean1);
        hexes.add(hexBean2);
        hexes.add(hexBean3);

        return hexes;
    }
}