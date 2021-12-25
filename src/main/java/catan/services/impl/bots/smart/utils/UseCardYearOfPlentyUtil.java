package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameUserBean;

import java.util.ArrayList;
import java.util.List;

public class UseCardYearOfPlentyUtil {
    public static List<String> getTwoRequiredResources(GameUserBean player) {
        //TODO: return required resources
        List<String> resources = new ArrayList<String>();
        resources.add(HexType.WOOD.getPatternName());
        resources.add(HexType.BRICK.getPatternName());

        return resources;
    }
}
