package catan.services;

public enum NodePosition {
    UP(0, -1, 1, -1),
    RIGHT_UP(1, -1, 1, 0),
    RIGHT_DOWN(1, 0, 0, 1),
    DOWN(0, 1, -1, 1),
    LEFT_DOWN(-1, 1, -1, 0),
    LEFT_UP(-1, 0, 0, -1);
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
