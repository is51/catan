package catan.domain.model.dashboard.types;

import catan.domain.model.game.types.GameStage;

public enum NodeBuiltType{
    SETTLEMENT(1),
    CITY(2);

    private final int resourceQuantityToAdd;

    NodeBuiltType(int resourceQuantityToAdd) {
        this.resourceQuantityToAdd = resourceQuantityToAdd;
    }

    public int getResourceQuantityToAdd(GameStage stage) {
        return GameStage.MAIN.equals(stage)
                ? this.resourceQuantityToAdd
                : 1;
    }
}
