package catan.domain.model.dashboard;

import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;

public class NodeBean {
    private int id;
    private GameBean game;
    private NodePortType port;
    private NodeBuiltType built;
    private GameUserBean buildingOwner;

    private HexBean upHex;
    private HexBean rightDownHex;
    private HexBean leftDownHex;

    private EdgeBean rightUpEdge;
    private EdgeBean dowEdge;
    private EdgeBean leftUpEdge;

}
