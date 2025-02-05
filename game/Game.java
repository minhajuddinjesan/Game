package be.kdg.oopswetried.game;
import be.kdg.oopswetried.controller.GameController;
import be.kdg.oopswetried.controller.PlayerController;
import be.kdg.oopswetried.model.Level;
import be.kdg.oopswetried.model.Player;
import be.kdg.oopswetried.model.Vehicle;
import be.kdg.oopswetried.utils.DurationUtils;
import be.kdg.oopswetried.utils.StringUtils;

import java.math.BigInteger;
import java.time.*;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Game {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GameController gameController = new GameController();
    private static final PlayerController playerController = new PlayerController();

    private int id;
    private Player player;
    private Level level;
    private int movesMade;
    private BigInteger duration;
    private Map<String, Map<String, Integer>> savedVehiclesState;
    private boolean completed;

    private Instant startTime;

    public Game(Player player, Level level) {
        this.player = player;
        this.level = level;
        movesMade = 0;
        duration = BigInteger.ZERO;
        completed = false;
        savedVehiclesState = new HashMap<>();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMovesMade(int movesMade) {
        this.movesMade = movesMade;
    }

    public void addMovesMade() {
        movesMade++;
    }

    public int getMovesMade() {
        return movesMade;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setDuration(BigInteger duration) {
        this.duration = duration;
    }

    public Map<String, Map<String, Integer>> getSavedVehiclesState() {
        return savedVehiclesState;
    }

    public void setSavedVehiclesState(Map<String, Map<String, Integer>> savedVehiclesState) {
        this.savedVehiclesState = savedVehiclesState;
    }

    public void start() {
        startTime = Instant.now().minusSeconds(Long.parseLong(duration.toString()));
        boolean running = true;

        load();

        while (running) {
            GameStatus status = move();

            if (status == GameStatus.GAME_OVER) {
                running = false;
            } else if (status == GameStatus.SAVE_GAME) {
                save();
                return;
            }
        }

        end();
    }

    private void printMainInformationAndUpdateDuration() {
        StringUtils.emptyTerminal();
        StringUtils.printRushHourLogo(player);

        duration = BigInteger.valueOf(Duration.between(startTime, Instant.now()).toSeconds());

        System.out.println("Level: " + level.getNumber() + " - " + level.getDifficulty() + "\t\tMoves made: " + movesMade + "\t\tDuration: " + DurationUtils.formatSecondsToPrettyDuration(duration) + "\n");
        System.out.println(level.getBoard().getBoard());
    }

    private GameStatus move() {
        int color = -1;
        Vehicle vehicle = null;
        String errorMessage = "";

        // part 1

        do {
            printMainInformationAndUpdateDuration();

            if (!errorMessage.trim().isEmpty()) {
                System.out.println(errorMessage + "\n");
            }

            System.out.print("Choose vehicle to move (or 0 to save the game): ");

            try {
                color = scanner.nextInt();
            } catch (InputMismatchException e) {
                errorMessage = "Invalid car color!";
                scanner.nextLine(); // fixes weird loop bug, if it works it works
                continue;
            }

            if (color == 0) {
                return GameStatus.SAVE_GAME;
            }

            vehicle = level.getBoard().getVehicle(color);

            if (vehicle == null) {
                errorMessage = "Invalid car color!";
                color = -1;
            }
        } while (color == -1);

        // part 2

        errorMessage = "";
        int option = -1;

        do {
            printMainInformationAndUpdateDuration();
            if (!errorMessage.trim().isEmpty()) {
                System.out.println(errorMessage + "\n");
            }
            System.out.println("Chosen car: " + vehicle.getColor());
            System.out.println("\n1 - Move car " + (vehicle.isVertical() ? "up" : "left"));
            System.out.println("2 - Move car " + (vehicle.isVertical() ? "down" : "right"));
            System.out.println("3 - Choose another car");

            System.out.print("\nChoose: ");

            try {
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                errorMessage = "Invalid option!";
                scanner.nextLine(); // fixes weird loop bug, if it works it works
                continue;
            }

            switch (option) {
                case 1 -> {
                    level.getBoard().moveVehicle(vehicle, false);
                    addMovesMade();
                }
                case 2 -> {
                    level.getBoard().moveVehicle(vehicle, true);
                    addMovesMade();
                }
                case 3 -> {
                    return GameStatus.PLAYING;
                }
                default -> {
                    errorMessage = "Invalid option!";
                    option = -1;
                }
            }
        } while (option == -1);

        if (level.getBoard().isLevelFinished()) {
            return GameStatus.GAME_OVER;
        }

        return GameStatus.PLAYING;
    }

    private void load() {
        if (getSavedVehiclesState().isEmpty()) {
            return;
        }

        Map<String, Map<String, Integer>> savedProgression = getSavedVehiclesState();

        for (Map.Entry<String, Map<String, Integer>> entry : savedProgression.entrySet()) {
            Map<String, Integer> vehicleData = entry.getValue();
            int startingPositionX = vehicleData.get("starting_position_x");
            int startingPositionY = vehicleData.get("starting_position_y");

            Vehicle vehicle = getLevel().getBoard().getVehicle(Integer.parseInt(entry.getKey()));

            if (vehicle != null) {
                vehicle.setStartingPosition(startingPositionX, startingPositionY);
            }
        }

        getLevel().getBoard().setupLayout();
    }

    private void save() {
        printMainInformationAndUpdateDuration();

        System.out.println("Level progress was saved!");
        System.out.println("\nLevel: " + level.getNumber() + " - " + level.getDifficulty());
        System.out.println("Total moves made: " + movesMade);
        System.out.println("Total duration: " + DurationUtils.formatSecondsToPrettyDuration(duration));

        savedVehiclesState.clear();

        for (Vehicle vehicle : level.getBoard().getVehicles()) {
            Map<String, Integer> coordinates = new HashMap<>();
            coordinates.put("starting_position_x", vehicle.getStartingPositionX());
            coordinates.put("starting_position_y", vehicle.getStartingPositionY());
            savedVehiclesState.put(String.valueOf(vehicle.getColor()), coordinates);
        }

        gameController.update(this);

        pressEnterToContinue();
    }

    private void end() {
        printMainInformationAndUpdateDuration();

        System.out.println("You have finished the level, congratulations!");
        System.out.println("\nLevel: " + level.getNumber() + " - " + level.getDifficulty());
        System.out.println("Total moves made: " + movesMade);
        System.out.println("Total duration: " + DurationUtils.formatSecondsToPrettyDuration(duration));

        player.addTotalMoves(movesMade);
        player.addTimeSpent(duration);
        playerController.update(player);

        completed = true;
        savedVehiclesState = new HashMap<>();
        movesMade = 0;
        duration = BigInteger.ZERO;
        gameController.update(this);

        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.println("\nPress enter to continue...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private enum GameStatus { PLAYING, GAME_OVER, SAVE_GAME }
}