package catan.services;

public enum EdgePosition {
    TOP_LEFT(0, -1),
    TOP_RIGHT(1, -1),
    RIGHT(1, 0),
    BOTTOM_RIGHT(0, 1),
    BOTTOM_LEFT(-1, 1),
    LEFT(-1, 0);

    private final int outerNeighborHexXShift;
    private final int outerNeighborHexYShift;

    EdgePosition(int outerNeighborHexXShift, int outerNeighborHexYShift) {
        this.outerNeighborHexXShift = outerNeighborHexXShift;
        this.outerNeighborHexYShift = outerNeighborHexYShift;
    }

    public int getOuterNeighborHexXShift() {
        return outerNeighborHexXShift;
    }

    public int getOuterNeighborHexYShift() {
        return outerNeighborHexYShift;
    }
}
