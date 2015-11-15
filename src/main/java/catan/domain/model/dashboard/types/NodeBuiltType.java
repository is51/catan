package catan.domain.model.dashboard.types;

public enum NodeBuiltType{
    SETTLEMENT,
    CITY;

    public int resourceQuantityToAdd() {
        return this == SETTLEMENT ? 1 : 2;
    }
}
