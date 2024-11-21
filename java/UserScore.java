package com.example.gamecomplex;

public class UserScore {
    private String userId;
    private String userName;
    private int highScore;

    public UserScore(String userId, String userName, int highScore) {
        this.userId = userId;
        this.userName = userName;
        this.highScore = highScore;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getHighScore() {
        return highScore;
    }
}
