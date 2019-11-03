package com.example.nomik.boardgamemanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FragmentSearch fragmentSearch;
    private FragmentSearchGame fragmentSearchGame;
    private FragmentGame fragmentGame;
    private FragmentMap fragmentMap;
    private String myName = new String();
    private ArrayList<FragmentSearchData> recentSearch = new ArrayList<>();
    private int fragmentNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("bgm", MODE_PRIVATE);
        myName = sp.getString("name", "");
        try {
            JSONArray jsonArray = new JSONArray(sp.getString("recentSearch", "[]"));
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FragmentSearchData fragmentSearchData = new FragmentSearchData(jsonObject.getString("name"), jsonObject.getString("id"));
                recentSearch.add(fragmentSearchData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(myName.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("당신의 이름을 알려주세요");
            final ClearEditText editText = new ClearEditText(MainActivity.this);
            editText.setText(myName);
            builder.setView(editText);
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(editText.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        myName = editText.getText().toString();
                        SetFragment("update");
                        dialogInterface.dismiss();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        ImageButton imageButton = findViewById(R.id.button_personal);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("프로필 설정");
                final ClearEditText editText = new ClearEditText(MainActivity.this);
                final String myNameOld = myName;
                editText.setText(myName);
                editText.setSelection(myName.length());
                builder.setView(editText);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myName = editText.getText().toString();
                        if(!myName.equals(myNameOld)) {
                            Thread thread = new Thread(){
                                @Override
                                public void run() {
                                    if(nameUpdate(myNameOld)) {
                                        SetFragment("update");
                                    } else {
                                        myName = myNameOld;
                                    }
                                }
                            };
                            thread.start();
                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        imageButton = findViewById(R.id.button_help);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                switch (fragmentNum) {
                    case 0:
                        bundle.putInt("fragmentNum", 0);
                        break;
                    case 1:
                        bundle.putInt("fragmentNum", 1);
                        break;
                    case 2:
                        bundle.putInt("fragmentNum", 2);
                        break;
                    case 3:
                        bundle.putInt("fragmentNum", 3);
                        break;
                    default:
                        break;
                }
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        fragmentSearch = new FragmentSearch();
        fragmentSearchGame = new FragmentSearchGame();
        fragmentGame = new FragmentGame();
        fragmentMap = new FragmentMap();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragmentSearch).commit();
        fragmentNum = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sp = getSharedPreferences("bgm", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < recentSearch.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", recentSearch.get(i).getName());
                jsonObject.put("id", recentSearch.get(i).getGameid());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        editor.putString("name", myName);
        editor.putString("recentSearch", jsonArray.toString());
        editor.commit();
    }

    public void onClickMenuButton(View view) {

        switch (view.getId()) {
            case R.id.button_search: {
                SetFragment("search");
                break;
            }
            case R.id.button_game: {
                SetFragment("game");
                break;
            }
            case R.id.button_map: {
                SetFragment("map");
                break;
            }
        }
    }

    public void SetFragment(String fragmentname) {
        ImageButton search = findViewById(R.id.button_search);
        ImageButton game = findViewById(R.id.button_game);
        ImageButton map = findViewById(R.id.button_map);

       FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch(fragmentname) {
            case "search": {
                fragmentNum = 0;
                search.setImageResource(R.drawable.button_search_selected);
                game.setImageResource(R.drawable.button_game_unselected);
                map.setImageResource(R.drawable.button_map_unselected);

                fragmentTransaction.replace(R.id.fragment_frame, fragmentSearch).commit();
                break;
            }
            case "game": {
                fragmentNum = 1;
                search.setImageResource(R.drawable.button_search_unselected);
                game.setImageResource(R.drawable.button_game_selected);
                map.setImageResource(R.drawable.button_map_unselected);

                fragmentTransaction.replace(R.id.fragment_frame, fragmentGame).commit();
                break;
            }
            case "map": {
                fragmentNum = 2;
                search.setImageResource(R.drawable.button_search_unselected);
                game.setImageResource(R.drawable.button_game_unselected);
                map.setImageResource(R.drawable.button_map_selected);

               fragmentTransaction.replace(R.id.fragment_frame, fragmentMap).commit();
                break;
            }
            case "update": {
                switch (fragmentNum) {
                    case 0:
                        fragmentSearch = new FragmentSearch();
                        fragmentTransaction.replace(R.id.fragment_frame, fragmentSearch).commit();
                        break;
                    case 1:
                        fragmentGame = new FragmentGame();
                        fragmentTransaction.replace(R.id.fragment_frame, fragmentGame).commit();
                        break;
                    case 2:
                        fragmentMap = new FragmentMap();
                        fragmentTransaction.replace(R.id.fragment_frame, fragmentMap).commit();
                        break;
                }
                break;
            }
        }
    }

    public void FragmentGameUpdate() {
        fragmentGame = new FragmentGame();
    }

    public void SetFragment(String fragmentname, String _name, String _id) {
        final String name = _name;
        final String id = _id;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch(fragmentname) {
            case "search_game": {
                fragmentNum = 3;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject toSend = new JSONObject();
                        try {
                            toSend.put("id", id);
                            toSend.put("name", name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ServerConnect serverConnect = new ServerConnect("search", toSend.toString());
                        JSONObject result = serverConnect.getJSONObjectWithSend();

                        String resultString = new String();
                        try {
                            resultString = result.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(resultString.equals("success")) {
                            fragmentSearchGame = new FragmentSearchGame();
                            Bundle bundle = new Bundle();
                            bundle.putString("myName", myName);
                            bundle.putString("name", name);
                            bundle.putString("id", id);
                            try {
                                bundle.putString("year", result.getString("year"));
                                bundle.putString("minplaytime", result.getString("playTimeMin"));
                                bundle.putString("maxplaytime", result.getString("playTimeMax"));
                                bundle.putString("minplayer", result.getString("playerNumMin"));
                                bundle.putString("maxplayer", result.getString("playerNumMax"));
                                bundle.putString("age", result.getString("age"));
                                bundle.putString("publisher", result.getString("publisher"));
                                bundle.putString("description", result.getString("description"));
                                bundle.putString("imageurl", result.getString("imageURL"));
                                bundle.putString("hasTable", result.getString("hasTable"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            fragmentSearchGame.setArguments(bundle);
                        } else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "서버에 연결할수 없습니다.", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fragmentTransaction.replace(R.id.fragment_frame, fragmentSearchGame).addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
        }
    }

    private boolean nameUpdate(String oldName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldName", oldName);
            jsonObject.put("newName", myName);
            jsonObject.put("phoneNum", getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ServerConnect serverConnect = new ServerConnect("nameUpdate", jsonObject.toString());
        if(serverConnect.send()){
            Looper.prepare();
            Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return true;
        } else {
            Looper.prepare();
            Toast.makeText(this, "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        return false;
    }

    public String getMyName(){
        return myName;
    }

    public void addRecentSearch(String name, String id) {
        FragmentSearchData fragmentSearchData = new FragmentSearchData(name, id);
        if(!recentSearch.contains(fragmentSearchData)) {
            if(recentSearch.size() < 5) {
                recentSearch.add(fragmentSearchData);
            } else {
                recentSearch.remove(0);
                recentSearch.add(fragmentSearchData);
            }
        }
    }

    public ArrayList<FragmentSearchData> getRecentSearch() {
        return recentSearch;
    }

    private String getPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
        String pNumber = tm.getLine1Number();
        return pNumber;
    }
}
