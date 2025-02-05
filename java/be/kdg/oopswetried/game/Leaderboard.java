package be.kdg.oopswetried.game;
import java.util.*;

public class Leaderboard {
    private final Map<String, String[]> leaderboardEntries;

    public Leaderboard() {
        leaderboardEntries = new HashMap<>();
    }

    public void addEntry(String key, String value) {
        leaderboardEntries.put(key, new String[]{value});
    }

    public Map<String, String[]> getLeaderboardEntries() {
        return leaderboardEntries;
    }

}
