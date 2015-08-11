package catan.services;

public enum NodePosition {
    UP(0, -1, 1, -1),
    RIGHT_UP(1, -1, 1, 0),
    RIGHT_DOWN(1, 0, 0, 1),
    DOWN(0, 1, -1, 1),
    LEFT_DOWN(-1, 1, -1, 0),
    LEFT_UP(-1, 0, 0, -1);
    private final int leftHexX;
    private final int leftHexY;
    private final int rightHexX;
    private final int rightHexY;

    NodePosition(int leftHexX, int leftHexY, int rightHexX, int rightHexY) {
        this.leftHexX = leftHexX;
        this.leftHexY = leftHexY;
        this.rightHexX = rightHexX;
        this.rightHexY = rightHexY;
    }

    public int getLeftHexX() {
        return leftHexX;
    }

    public int getLeftHexY() {
        return leftHexY;
    }

    public int getRightHexX() {
        return rightHexX;
    }

    public int getRightHexY() {
        return rightHexY;
    }
}
