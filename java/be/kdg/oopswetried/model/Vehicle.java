package be.kdg.oopswetried.model;

public class Vehicle {
    private int id;
    private int color;
    private int length;
    private boolean isVertical;
    private int startingPositionX;
    private int startingPositionY;

    public Vehicle(int color, int length, boolean isVertical, int startingPositionX, int startingPositionY) {
        if (startingPositionX < 0 || startingPositionY < 0) {
            throw new IllegalArgumentException("Error creating vehicle. Starting positions must be positive!");
        }

        if (color < 1) {
            throw new IllegalArgumentException("Error creating vehicle. Color must be positive!");
        }

        this.color = color;
        this.length = length;
        this.isVertical = isVertical;
        this.startingPositionX = startingPositionX;
        this.startingPositionY = startingPositionY;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public void setStartingPosition(int x, int y) {
        startingPositionX = x;
        startingPositionY = y;
    }

    public void setStartingPositionX(int x) {
        startingPositionX = x;
    }

    public void setStartingPositionY(int y) {
        startingPositionY = y;
    }

    public int getStartingPositionX() {
        return startingPositionX;
    }

    public int getStartingPositionY() {
        return startingPositionY;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", length=" + length +
                ", isVertical=" + isVertical +
                ", startingPositionX=" + startingPositionX +
                ", startingPositionY=" + startingPositionY +
                '}';
    }
}
