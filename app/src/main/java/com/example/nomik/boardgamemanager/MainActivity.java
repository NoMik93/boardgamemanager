package com.example.nomik.boardgamemanager;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FragmentSearch fragmentSearch;
    private FragmentSearchGame fragmentSearchGame;
    private FragmentGame fragmentGame;
    private FragmentMap fragmentMap;
    private String myName = new String();
    private ArrayList<FragmentSearchData> recentSearch = new ArrayList<>();

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
        ImageButton imageButton = findViewById(R.id.button_personal);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("프로필 설정");
                final ClearEditText editText = new ClearEditText(MainActivity.this);
                editText.setText(myName);
                builder.setView(editText);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myName = editText.getText().toString();
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

        fragmentSearch = new FragmentSearch();
        fragmentSearchGame = new FragmentSearchGame();
        fragmentGame = new FragmentGame();
        fragmentMap = new FragmentMap();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragmentSearch).commit();
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
        ImageButton search = (ImageButton)findViewById(R.id.button_search);
        ImageButton game = (ImageButton)findViewById(R.id.button_game);
        ImageButton map = (ImageButton)findViewById(R.id.button_map);

       FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch(fragmentname) {
            case "search": {
                search.setImageResource(R.drawable.button_search_selected);
                game.setImageResource(R.drawable.button_game_unselected);
                map.setImageResource(R.drawable.button_map_unselected);

                fragmentTransaction.replace(R.id.fragment_frame, fragmentSearch).commit();
                break;
            }
            case "game": {
                search.setImageResource(R.drawable.button_search_unselected);
                game.setImageResource(R.drawable.button_game_selected);
                map.setImageResource(R.drawable.button_map_unselected);

                fragmentTransaction.replace(R.id.fragment_frame, fragmentGame).commit();
                break;
            }
            case "map": {
                search.setImageResource(R.drawable.button_search_unselected);
                game.setImageResource(R.drawable.button_game_unselected);
                map.setImageResource(R.drawable.button_map_selected);

               fragmentTransaction.replace(R.id.fragment_frame, fragmentMap).commit();
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
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection conn = null;
                        try {
                            String year = null;
                            String minplaytime = null;
                            String maxplaytime = null;
                            String minplayer = null;
                            String maxplayer = null;
                            String age = null;
                            String imageurl = null;
                            String publisher = null;
                            String description = null;
                            String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
                            URL url = new URL(urlString);
                            conn = (HttpURLConnection) url.openConnection();

                            conn.setReadTimeout(3000);
                            conn.setConnectTimeout(3000);
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            conn.setRequestProperty("mode", "search");
                            conn.setRequestProperty("id", id);
                            conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
                            conn.setRequestProperty("Accept", "application/json");

                            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
                            osw.write(name);
                            osw.close();

                            int responseCode = conn.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                InputStream is = conn.getInputStream();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] byteBuffer = new byte[1024];
                                byte[] byteData = null;
                                int length = 0;
                                while ((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                                    baos.write(byteBuffer, 0, length);
                                }
                                byteData = baos.toByteArray();
                                String responseString = new String(byteData);
                                JSONObject jsonObject = new JSONObject(responseString);
                                year = jsonObject.getString("year");
                                minplaytime = jsonObject.getString("playTimeMin");
                                maxplaytime = jsonObject.getString("playTimeMax");
                                minplayer = jsonObject.getString("playerNumMin");
                                maxplayer = jsonObject.getString("playerNumMax");
                                age = jsonObject.getString("age");
                                imageurl = jsonObject.getString("imageURL");
                                publisher = jsonObject.getString("publisher");
                                description = jsonObject.getString("description");
                            } else {
                                Toast.makeText(MainActivity.this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                            fragmentSearchGame = new FragmentSearchGame();
                            Bundle bundle = new Bundle();
                            bundle.putString("myName", myName);
                            bundle.putString("name", name);
                            bundle.putString("id", id);
                            bundle.putString("year", year);
                            bundle.putString("minplaytime", minplaytime);
                            bundle.putString("maxplaytime", maxplaytime);
                            bundle.putString("minplayer", minplayer);
                            bundle.putString("maxplayer", maxplayer);
                            bundle.putString("age", age);
                            bundle.putString("publisher", publisher);
                            bundle.putString("description", description);
                            bundle.putString("imageurl", imageurl);
                            fragmentSearchGame.setArguments(bundle);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            if(conn != null)
                                conn.disconnect();
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
}
