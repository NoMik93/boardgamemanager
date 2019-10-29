package com.example.nomik.boardgamemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private String myName;
    private String gameName;
    private String gameID;
    private ArrayList<String> players;
    private String time;
    private FragmentContact contact;
    private FragmentGaming gaming;
    private FragmentScore score;
    private TextView textViewTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contact = new FragmentContact();
        gaming = new FragmentGaming();
        score = new FragmentScore();
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        myName = intent.getStringExtra("myName");
        gameName = intent.getStringExtra("name");
        gameID = intent.getStringExtra("id");

        textViewTitle = findViewById(R.id.textview_game_name);
        textViewTitle.setText(gameName);
        textViewTitle = findViewById(R.id.textview_game_title);
        SetFragment("contact");
    }

    public void SetFragment(String fragmentname) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch(fragmentname) {
            case "contact": {
                fragmentTransaction.replace(R.id.game_fragment_frame, contact);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                textViewTitle.setText("플레이어 설정");
                break;
            }
            case "gaming": {
                fragmentTransaction.replace(R.id.game_fragment_frame, gaming);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                textViewTitle.setText("게임 진행 중");
                break;
            }
            case "score": {
                fragmentTransaction.replace(R.id.game_fragment_frame, score);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                textViewTitle.setText("점수 계산");
                break;
            }
            case "complete": {
                setResult(RESULT_OK);
                finish();
                break;
            }
        }
    }

    public void SetPlayers(ArrayList<String> player) {
        players = new ArrayList<>();
        players.addAll(player);
    }

    public void SetTime(String time) {
        this.time = time;
    }
    public ArrayList<String> getPlayers(){
        return players;
    }
    public String getGameID(){
        return gameID;
    }
    public String getGameName(){
        return gameName;
    }
    public String getTime(){
        return time;
    }
    public String getMyName() { return myName; }
}
