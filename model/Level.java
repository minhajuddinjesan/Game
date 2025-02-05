package be.kdg.oopswetried.model;

public class Level {
    private int id;
    private Board board;
    private int number;
    private Difficulty difficulty;

    public Level(Board board, int number, Difficulty difficulty) {
        this.board = board;
        this.number = number;
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    // suggestion multiplier, discuss with team, based on data and ai requirement for my task - eugen
    public enum Difficulty {
        UNKNOWN("Unknown"),
        EASY("Easy"),
        MEDIUM("Medium"),
        HARD("Hard");

        private final String difficultyName;

        Difficulty(String difficultyName) {
            this.difficultyName = difficultyName;
        }

        public static Difficulty fromString(String text) {
            for (Difficulty difficulty : Difficulty.values()) {
                if (difficulty.difficultyName.equals(text)) {
                    return difficulty;
                }
            }
            return null;
        }

        public static Difficulty fromStringIgnoreCase(String text) {
            for (Difficulty difficulty : Difficulty.values()) {
                if (difficulty.difficultyName.equalsIgnoreCase(text)) {
                    return difficulty;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return difficultyName;
        }
    }

    @Override
    public String toString() {
        return "Level{" +
                "id=" + id +
                ", board=" + board +
                ", number=" + number +
                ", difficulty=" + difficulty +
                '}';
    }
}