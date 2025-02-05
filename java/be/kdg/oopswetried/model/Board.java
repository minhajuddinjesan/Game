package be.kdg.oopswetried.model;

import java.util.Arrays;
import java.util.List;

public class Board {
    private int id;
    private int size;
    private List<Vehicle> vehicles;
    private int[][] layout;

    public Board(int size, List<Vehicle> vehicles) {
        this.size = size;
        setVehicles(vehicles);
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        setupLayout();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setLayout(int[][] layout) {
        this.layout = layout;
    }

    public void setupLayout() {
        this.layout = new int[size][size];
        boolean isMainCarSetup = false;

        if (vehicles.isEmpty()) {
            throw new RuntimeException("Error creating board layout. No vehicles found!");
        }


        for (Vehicle vehicle : vehicles) {
            int color = vehicle.getColor();
            int x = vehicle.getStartingPositionX();
            int y = vehicle.getStartingPositionY();
            int length = vehicle.getLength();
            boolean isVertical = vehicle.isVertical();

            // check if car inside bounds
            if ((isVertical && y + length > size) || (!isVertical && x + length > size)) {
                throw new RuntimeException("Error creating board layout. Vehicle coordinates are out of bounds!");
            }

            // check if main car is setup correctly
            if (color == 1 && length != 2) {
                throw new RuntimeException("Error creating board layout. Main car length isn't correct!");
            }

            for (int i = 0; i < length; i++) {
                int row = isVertical ? y + i : y;
                int column = isVertical ? x : x + i;

                if (layout[row][column] != 0) {
                    throw new RuntimeException("Error creating board layout. Vehicle coordinates are overlapping!");
                }

                layout[row][column] = color;
            }
        }

        // check main car again if on row 3
        for (int i = 0; i < size; i++) {
            if (layout[2][i] == 1) {
                isMainCarSetup = true;
                break;
            }
        }

        if (!isMainCarSetup) {
            throw new RuntimeException("Error creating board layout. Main car coordinates were not setup correctly!");
        }
    }

    public void moveVehicle(Vehicle vehicle, boolean forward) {
        int direction = forward ? 1 : -1;
        int newPosition;

        if (vehicle.isVertical()) {
            newPosition = vehicle.getStartingPositionY();
        } else {
            newPosition = vehicle.getStartingPositionX();
        }

        // loop car move until it can't move anymore, sliding it until it hits either the bounds or another car
        while (isValidMove(vehicle, newPosition + direction)) {
            newPosition += direction;
        }

        int oldX = vehicle.getStartingPositionX();
        int oldY = vehicle.getStartingPositionY();

        // clear old position
        for (int i = 0; i < vehicle.getLength(); i++) {
            int row = vehicle.isVertical() ? oldY + i : oldY;
            int col = vehicle.isVertical() ? oldX : oldX + i;
            layout[row][col] = 0;
        }

        // set new positions
        for (int i = 0; i < vehicle.getLength(); i++) {
            int row = vehicle.isVertical() ? newPosition + i : oldY;
            int col = vehicle.isVertical() ? oldX : newPosition + i;
            layout[row][col] = vehicle.getColor();
        }

        if (vehicle.isVertical()) {
            vehicle.setStartingPositionY(newPosition);
        } else {
            vehicle.setStartingPositionX(newPosition);
        }
    }

    private boolean isValidMove(Vehicle vehicle, int newPosition) {
        // check if car move will get it out of bounds
        if (newPosition < 0 || newPosition + vehicle.getLength() > size) {
            return false;
        }

        // collision check
        for (int i = 0; i < vehicle.getLength(); i++) {
            int row = vehicle.isVertical() ? newPosition + i : vehicle.getStartingPositionY();
            int col = vehicle.isVertical() ? vehicle.getStartingPositionX() : newPosition + i;

            // if pos isn't 0 then there is another car there
            if (layout[row][col] != 0 && layout[row][col] != vehicle.getColor()) {
                return false;
            }
        }

        return true;
    }

    public boolean isLevelFinished() {
        Vehicle mainCar = getVehicle(1);

        if (mainCar.getStartingPositionY() == 2 && mainCar.getStartingPositionX() == size - 2) {
            for (int i = 0; i < mainCar.getLength(); i++) {
                int row = mainCar.isVertical() ? mainCar.getStartingPositionY() + i : mainCar.getStartingPositionY();
                int col = mainCar.isVertical() ? mainCar.getStartingPositionX() : mainCar.getStartingPositionX() + i;
                layout[row][col] = 0;
            }

            return true;
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vehicle getVehicle(int color) {
        return vehicles.stream().filter(vehicle -> vehicle.getColor() == color).findFirst().orElse(null);
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public int[][] getLayout() {
        return layout;
    }

    public String getBoard() {
        StringBuilder board = new StringBuilder();

        for (int i = 0; i < size; i++) {
            board.append("+").append("----+".repeat(size)).append("\n");
            for (int j = 0; j < size; j++) {
                board.append(String.format("| %-3s", layout[i][j] == 0 ? " " : Integer.toString(layout[i][j])));
            }
            board.append((i != 2) ? "|" : "").append("\n");
        }

        board.append("+").append("----+".repeat(size)).append("\n");

        return board.toString();
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", vehicles=" + vehicles +
                ", layout=" + Arrays.toString(layout) +
                '}';
    }
}
