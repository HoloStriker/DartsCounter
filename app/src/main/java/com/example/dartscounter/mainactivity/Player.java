package com.example.dartscounter.mainactivity;

public class Player implements Comparable<Player>{
    private String name;
    private int currentScore;
    private String[] tries;
    private int dartsCount;
    private float currentAvg;

    public Player(String name) {
        this.name = name;
        this.currentScore = 0;
        this.tries = new String[]{"-", "-", "-"};
        this.dartsCount = 0;
        this.currentAvg = 0;
    }

    public int getDartsCount() {
        return dartsCount;
    }

    public void setDartsCount(int dartsCount) {
        this.dartsCount = dartsCount;
    }

    public void increaseDartsCount() {
        this.dartsCount++;
    }

    public void decreaseDartsCount() {
        this.dartsCount--;
    }

    public float getCurrentAvg() {
        return currentAvg;
    }

    public void setCurrentAvg(float currentAvg) {
        this.currentAvg = currentAvg;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public String getName() {
        return name;
    }

    public String[] getTries() {
        return tries;
    }

    public void setTry(int index, String tr) {
        this.tries[index] = tr;
    }

    public void setTries(String[] tries) {
        this.tries[0] = tries[0];
        this.tries[1] = tries[1];
        this.tries[2] = tries[2];
    }

    @Override
    public int compareTo(Player playerToCompare) {
        return this.currentScore - playerToCompare.getCurrentScore();
    }
}
