package be.kdg.oopswetried.game;

import be.kdg.oopswetried.controller.GameController;
import be.kdg.oopswetried.controller.LevelController;
import be.kdg.oopswetried.controller.PlayerController;
import be.kdg.oopswetried.controller.LeaderboardController;
import be.kdg.oopswetried.model.Level;
import be.kdg.oopswetried.model.Player;
import be.kdg.oopswetried.utils.StringUtils;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PlayerController playerController = new PlayerController();
    private static final LevelController levelController = new LevelController();
    private static final GameController gameController = new GameController();

    private Player currentPlayer;

    public Menu() {
        currentPlayer = null;
        viewPlayerMenu();
        viewMainMenu();
    }

    private void viewPlayerMenu() {
        String username = "";
        int choice = -1;
        String errorMessage = "";

        do {
            StringUtils.printRushHourLogo();

            if (!errorMessage.isEmpty()) {
                System.out.println("\n" + errorMessage);
            }

            System.out.print("Enter your username: ");
            username = scanner.nextLine().strip();

            if (username.length() > 30) {
                errorMessage = "Maximum 30 characters allowed!";
                StringUtils.emptyTerminal();
                continue;
            }

            if (username.contains(" ")) {
                errorMessage = "You can't have spaces in your username!";
                StringUtils.emptyTerminal();
                continue;
            }

            if (username.isEmpty()) {
                errorMessage = "Username can't be empty!";
                StringUtils.emptyTerminal();
                continue;
            }

            errorMessage = "";
            currentPlayer = playerController.getByUsername(username);

            if (currentPlayer == null) {
                do {
                    StringUtils.emptyTerminal();
                    StringUtils.printRushHourLogo();

                    System.out.println("No previous player found with username \"" + username + "\"!");
                    System.out.println("Would you like to start a new game session?");

                    System.out.println("\n1 - Yes");
                    System.out.println("2 - No");

                    if (!errorMessage.isEmpty()) {
                        System.out.println("\n" + errorMessage);
                    }

                    System.out.print("\nChoose: ");

                    try {
                        choice = scanner.nextInt();
                        scanner.nextLine();
                    } catch (InputMismatchException e) {
                        scanner.nextLine();
                        errorMessage = "Invalid choice!";
                        continue;
                    }

                    switch (choice) {
                        case 1 -> {
                            currentPlayer = new Player(username);
                            playerController.create(currentPlayer);
                            return;
                        }
                        case 2 -> {
                            username = "";
                            errorMessage = "";
                            StringUtils.emptyTerminal();
                        }
                        default -> {
                            errorMessage = "Invalid choice!";
                            choice = -1;
                        }
                    }
                } while (choice == -1);
            }
        } while (username.isEmpty() || currentPlayer == null);
    }

    private void viewMainMenu() {
        int choice = -1;
        String errorMessage = "";

        do {
            StringUtils.emptyTerminal();
            StringUtils.printRushHourLogo(currentPlayer);

            System.out.println("1 - Play");
            System.out.println("2 - Leaderboards and statistics");
            System.out.println("3 - How to play");
            System.out.println("0 - Quit game");

            if (!errorMessage.isEmpty()) {
                System.out.println("\n" + errorMessage);
            }

            System.out.print("\nChoose: ");

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // fixes weird loop bug, if it works it works
                errorMessage = "Invalid choice!";
                continue;
            }

            switch (choice) {
                case 1 -> viewPlayMenu();
                case 2 -> viewLeaderboardMenu();
                case 3 -> viewHowToPlayMenu();
                default -> errorMessage = "Invalid choice!";
            }

            if (choice != 0) {
                StringUtils.emptyTerminal();
            }
        } while (choice != 0);
    }

    private void viewPlayMenu() {
        int choice = -1;
        String errorMessage = "";

        List<Level> levels = levelController.getAll();

        do {
            StringUtils.emptyTerminal();
            StringUtils.printRushHourLogo(currentPlayer);

            System.out.println("No symbol - haven't played, ~ - continue playing, * - completed\n");

            int columns = 4;
            int rows = (int) Math.ceil(levelController.getAll().size() / (double) columns);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int index = j * rows + i;
                    if (index < levels.size()) {
                        Level level = levels.get(index);
                        Game game = gameController.getByPlayerAndLevel(currentPlayer, level);

                        String status = "";

                        if (game != null) {
                            status = game.isCompleted() ? "*" : !game.getSavedVehiclesState().isEmpty() ? "~" : "";
                        }

                        System.out.printf("%-20s", String.format("%d - %s %s", level.getNumber(), level.getDifficulty(), status));
                    }
                }
                System.out.println();
            }

            System.out.println("\n0 - Back to main menu");

            if (!errorMessage.isEmpty()) {
                System.out.println("\n" + errorMessage);
            }

            System.out.print("\nChoose: ");

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // fixes weird loop bug, if it works it works
                errorMessage = "Invalid choice!";
            }

            Level level = levelController.getByNumber(choice);

            if (level == null) {
                errorMessage = "Invalid choice!";
                continue;
            }

            if (choice == 0) {
                break;
            }

            Game game = gameController.getByPlayerAndLevel(currentPlayer, level);

            if (game == null) {
                game = new Game(currentPlayer, level);
                gameController.create(game);
            }

            game.start();
        } while (choice != 0);
    }

    private void viewLeaderboardMenu() {
        int choice = -1;
        String errorMessage = "";

        do {
            StringUtils.emptyTerminal();
            StringUtils.printRushHourLogo(currentPlayer);

            // first your stats
            // then leaderboard
            // then reset your game progress or go back to main menu
            // implementation
            System.out.println("Your stats:");
            StringUtils.printTable(new String[]{"Username", "Levels completed", "Total moves", "Total time spent"}, new String[][]{LeaderboardController.getIndividualLeaderboard(currentPlayer.getUsername())}, false);

            System.out.println("\nLeaderboard:");
            StringUtils.printTable(new String[]{"Username", "Levels completed", "Total moves", "Total time spent"}, LeaderboardController.getLeaderboard(), true);

            System.out.println("\n1 - Reset your game progress");
            System.out.println("0 - Back to main menu");

            if (!errorMessage.isEmpty()) {
                System.out.println("\n" + errorMessage);
            }

            System.out.print("\nChoose: ");

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // fixes weird loop bug, if it works it works
                errorMessage = "Invalid choice!";
            }

            if (choice == 1) {
                if (viewResetGameProgressMenu()) {
                    errorMessage = "Your game progress has been reset!";
                }
            } else {
                errorMessage = "Invalid choice!";
            }
        } while (choice != 0);
    }

    private boolean viewResetGameProgressMenu() {
        int choice = -1;
        String errorMessage = "";

        do {
            StringUtils.emptyTerminal();
            StringUtils.printRushHourLogo(currentPlayer);

            System.out.println("Are you sure you want to reset your progress?");

            System.out.println("\n1 - Yes");
            System.out.println("2 - No");

            if (!errorMessage.isEmpty()) {
                System.out.println("\n" + errorMessage);
            }

            System.out.print("\nChoose: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                errorMessage = "Invalid choice!";
                continue;
            }

            switch (choice) {
                case 1 -> {
                    playerController.reset(currentPlayer);
                    return true;
                }
                case 2 -> {
                    return false;
                }
                default -> {
                    errorMessage = "Invalid choice!";
                    choice = -1;
                }
            }
        } while (choice == -1);

        return true;
    }

    private void viewHowToPlayMenu() {
        int choice = -1;
        String errorMessage = "";

        do {
            StringUtils.emptyTerminal();
            StringUtils.printRushHourLogo(currentPlayer);

            System.out.println("Objective!");
            System.out.println("Drive the MAIN VEHICLE (numbered 1) through the Rush Hour exit gate.");
            System.out.println("The exit gate is located on the right side of the game board, a notch\non the side of the board.");

            System.out.println("\nRules!");
            System.out.println("1) Pick a Rush Hour level.");
            System.out.println("2) To play the game, slide the vehicles back and forth to clear a path\nfor the MAIN VEHICLE to get through the exit.");
            System.out.println("3) When the MAIN CAR exits the game, YOU WIN!");

            System.out.println("\nRemember!");
            System.out.println("Vehicles can only move in their tracks, sliding back and forth.");
            // System.out.println(""); // something related to score, I was thinking count time as score and multiply it by difficulty, duration * (1 - easy, 2 - medium, 3 - hard)

            System.out.println("\n0 - Back to main menu");

            if (!errorMessage.isEmpty()) {
                System.out.println("\n" + errorMessage);
            }

            System.out.print("\nChoose: ");

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // fixes weird loop bug, if it works it works
                errorMessage = "Invalid choice!";
            }

            if (choice != 0) {
                errorMessage = "Invalid choice!";
            }
        } while (choice != 0);
    }
}