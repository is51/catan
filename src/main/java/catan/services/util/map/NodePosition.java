package catan.services.util.map;

public enum NodePosition {
    TOP(0, -1, 1, -1),
    TOP_RIGHT(1, -1, 1, 0),
    BOTTOM_RIGHT(1, 0, 0, 1),
    BOTTOM(0, 1, -1, 1),
    BOTTOM_LEFT(-1, 1, -1, 0),
    TOP_LEFT(-1, 0, 0, -1);
    private final int leftNeighborHexXShift;
    private final int leftNeighborHexYShift;
    private final int rightNeighborHexXShift;
    private final int rightNeighborHexYShift;

    NodePosition(int leftNeighborHexXShift, int leftNeighborHexYShift, int rightNeighborHexXShift, int rightNeighborHexYShift) {
        this.leftNeighborHexXShift = leftNeighborHexXShift;
        this.leftNeighborHexYShift = leftNeighborHexYShift;
        this.rightNeighborHexXShift = rightNeighborHexXShift;
        this.rightNeighborHexYShift = rightNeighborHexYShift;
    }

    public int getLeftNeighborHexXShift() {
        return leftNeighborHexXShift;
    }

    public int getLeftNeighborHexYShift() {
        return leftNeighborHexYShift;
    }

    public int getRightNeighborHexXShift() {
        return rightNeighborHexXShift;
    }

    public int getRightNeighborHexYShift() {
        return rightNeighborHexYShift;
    }
}
