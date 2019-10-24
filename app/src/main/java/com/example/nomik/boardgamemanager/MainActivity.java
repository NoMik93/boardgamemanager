package com.example.nomik.boardgamemanager;

//import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;

public class MainActivity extends AppCompatActivity {
    FragmentSearch fragmentSearch;
    FragmentSearchGame fragmentSearchGame;
    FragmentGame fragmentGame;
    FragmentMap fragmentMap;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentSearch = new FragmentSearch();
        fragmentSearchGame = new FragmentSearchGame();
        fragmentGame = new FragmentGame();
        fragmentMap = new FragmentMap();

        context = this;


        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragmentSearch).commit();

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
                            URL url= new URL("https://www.boardgamegeek.com/xmlapi/boardgame/" + id + "?stats=1");

                            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = xmlPullParserFactory.newPullParser();
                            InputStream inputStream = url.openStream();
                            parser.setInput(new InputStreamReader(inputStream));

                            int eventType = parser.getEventType();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    String startTag = parser.getName();
                                    if(startTag.equals("yearpublished")) {
                                        year = parser.nextText();
                                    }
                                    if(startTag.equals("minplaytime")) {
                                        minplaytime = parser.nextText();
                                    }
                                    if(startTag.equals("maxplaytime")) {
                                        maxplaytime = parser.nextText();
                                    }
                                    if(startTag.equals("minplayers")) {
                                        minplayer = parser.nextText();
                                    }
                                    if(startTag.equals("maxplayers")) {
                                        maxplayer = parser.nextText();
                                    }
                                    if(startTag.equals("age")) {
                                        age = parser.nextText();
                                    }
                                    if(startTag.equals("image")) {
                                        imageurl = parser.nextText();
                                    }
                                    if(startTag.equals("boardgamepublisher")) {
                                        publisher = parser.nextText();
                                    }
                                    if(startTag.equals("description")) {
                                        description = parser.nextText();
                                    }
                                }
                                eventType = parser.next();
                            }
                            fragmentSearchGame = new FragmentSearchGame();
                            Bundle bundle = new Bundle();
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
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fragmentTransaction.replace(R.id.fragment_frame, fragmentSearchGame).addToBackStack(null).commit();
                break;
            }
        }
    }
}
