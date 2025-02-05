package be.kdg.oopswetried.model;
import be.kdg.oopswetried.utils.DurationUtils;

import java.math.BigInteger;

public class Player {
    private int id;
    private String username;
    private int totalMoves;
    private BigInteger totalTimeSpent;

    public Player(String username) {
        this.username = username;
        this.totalMoves = 0;
        this.totalTimeSpent = BigInteger.ZERO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public BigInteger getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }

    public void setTotalTimeSpent(BigInteger totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public void addTotalMoves(int totalMoves) {
        this.totalMoves += totalMoves;
    }

    public void addTimeSpent(BigInteger timeSpent) {
        totalTimeSpent = totalTimeSpent.add(timeSpent);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", totalMoves=" + totalMoves +
                ", totalTimeSpent=" + DurationUtils.formatSecondsToPrettyDuration(totalTimeSpent) +
                '}';
    }
}
