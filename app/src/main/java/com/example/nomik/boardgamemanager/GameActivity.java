package com.example.nomik.boardgamemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private String name;
    private String gameID;
    private ArrayList<String> players;
    private ArrayList<Integer> score;
    private String time;
    TextView textViewTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        gameID = intent.getStringExtra("id");

        textViewTitle = findViewById(R.id.textview_game_name);
        textViewTitle.setText(name);
        textViewTitle = findViewById(R.id.textview_game_title);
        SetFragment("contact");
    }

    public void SetFragment(String fragmentname) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch(fragmentname) {
            case "contact": {
                fragmentTransaction.replace(R.id.game_fragment_frame, new FragmentContact()).commit();
                textViewTitle.setText("플레이어 설정");
                break;
            }
            case "gaming": {
                fragmentTransaction.replace(R.id.game_fragment_frame, new FragmentGaming()).commit();
                textViewTitle.setText("게임 진행 중");
                break;
            }
            case "score": {
                fragmentTransaction.replace(R.id.game_fragment_frame, new FragmentScore()).commit();
                break;
            }
            case "complete": {
                this.finish();
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
}
