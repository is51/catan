package catan.services.util.play;

import catan.domain.model.dashboard.Building;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import org.slf4j.Logger;

public class ResourceUtil {

    public static void produceResources(HexBean sourceHex, Building<NodeBuiltType> consumingBuilding, Logger log) {
        HexType resourceType =  sourceHex.getResourceType();
        GameUserBean buildingOwner = consumingBuilding.getBuildingOwner();
        Resources userResources = buildingOwner.getResources();

        int currentResourceQuantity = userResources.quantityOf(resourceType);
        int resourceQuantityToAdd = consumingBuilding.getBuilt().getResourceQuantityToAdd(sourceHex.getGame().getStage());

        userResources.updateResourceQuantity(resourceType, currentResourceQuantity + resourceQuantityToAdd);

        log.debug("GameUser (name: " + buildingOwner.getUser().getUsername() + ", colorId: " + buildingOwner.getColorId() + ", id: " +buildingOwner.getGameUserId() + ")" +
                " got " + resourceQuantityToAdd + " " +  resourceType + " for " + consumingBuilding.getBuilt() + " at " + sourceHex);
    }
}
