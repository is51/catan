package catan.domain.model.dashboard.types;

public enum NodeBuiltType{
    SETTLEMENT(1),
    CITY(2);

    private final int resourceQuantityToAdd;

    NodeBuiltType(int resourceQuantityToAdd) {
        this.resourceQuantityToAdd = resourceQuantityToAdd;
    }

    public int defineResourceQuantityToAdd() {
        return this.resourceQuantityToAdd;
    }
}
