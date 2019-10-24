package com.example.nomik.boardgamemanager;

import java.io.Serializable;
import java.util.ArrayList;

public class FragmentGameItem implements Serializable {
    private String gameId;
    private String gameName;
    private String date;
    private String dateTime;
    private String playTime;
    private ArrayList<Player> players = new ArrayList<>();

    public FragmentGameItem(String gameId, String gameName, String date, String dateTime, String playTime) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.date = date;
        this.dateTime = dateTime;
        this.playTime = playTime;
    }
    public void addPlayer(String name, int score) {
        Player player = new Player(name, score);
        players.add(player);
    }
    public String getGameName() {
        return gameName;
    }
    public String getDate() {
        return date;
    }
    public String getDateTime() {
        return dateTime;
    }
    public String getPlayTime() {
        return playTime;
    }
    public String getPlayerName(int i) {
        return players.get(i).getName();
    }
    public int getPlayerScore(int i) {
        return players.get(i).getScore();
    }
    public int getPlayerNum() {
        return players.size();
    }
    private class Player {
        private String name;
        private int score;
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
        public String getName() {
            return name;
        }
        public int getScore(){
            return score;
        }
    }

}
