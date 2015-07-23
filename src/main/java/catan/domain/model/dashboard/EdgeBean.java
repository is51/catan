package catan.domain.model.dashboard;

import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;

public class EdgeBean {
    private int id;
    private GameBean game;
    private EdgeBuiltType built;
    private GameUserBean buildingOwner;

    private HexBean upHex;
    private HexBean downHex;

    private NodeBean leftNode;
    private NodeBean rightNode;
}
