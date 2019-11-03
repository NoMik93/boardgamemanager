package com.example.nomik.boardgamemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private String myName;
    private String gameName;
    private String gameID;
    private int hasTable;
    private ArrayList<String> players;
    private String time;
    private FragmentContact contact;
    private FragmentGaming gaming;
    private FragmentScore score;
    private TextView textViewTitle;
    private int fragmentNum = 0;
    private ArrayList<String> tables = new ArrayList<>();

    @Override
    public void onBackPressed() {
        switch (fragmentNum) {
            case 1:
                textViewTitle.setText("플레이어 설정");
                fragmentNum--;
                break;
            case 2:
                textViewTitle.setText("게임 진행 중");
                fragmentNum--;
                break;
        }
        super.onBackPressed();
    }

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
        hasTable = Integer.parseInt(intent.getStringExtra("hasTable"));

        ImageButton imageButton = findViewById(R.id.button_help);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                switch (fragmentNum) {
                    case 0:
                        bundle.putInt("fragmentNum", 4);
                        break;
                    case 1:
                        bundle.putInt("fragmentNum", 5);
                        break;
                    case 2:
                        bundle.putInt("fragmentNum", 6);
                        break;
                    default:
                        break;
                }
                Intent intent = new Intent(GameActivity.this, HelpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        textViewTitle = findViewById(R.id.textview_game_name);
        textViewTitle.setText(gameName);
        textViewTitle = findViewById(R.id.textview_game_title);
        SetFragment("contact");
    }

    public void SetFragment(String fragmentname) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch(fragmentname) {
            case "contact": {
                fragmentNum = 0;
                fragmentTransaction.replace(R.id.game_fragment_frame, contact);
                fragmentTransaction.commit();
                textViewTitle.setText("플레이어 설정");
                break;
            }
            case "gaming": {
                fragmentNum = 1;
                fragmentTransaction.replace(R.id.game_fragment_frame, gaming);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                textViewTitle.setText("게임 진행 중");
                break;
            }
            case "score": {
                fragmentNum = 2;
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

    public ArrayList<String> getTables() {
        return tables;
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
    public int getHasTable() { return hasTable; }
}
