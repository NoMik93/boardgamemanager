package com.example.nomik.boardgamemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class FragmentSearchGame extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Bitmap[] bitmap = new Bitmap[1];

        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_search_game, container, false);
        if (getArguments() != null) {
            TextView textView = view.findViewById(R.id.search_game_name);
            textView.setText(getArguments().getString("name"));
            textView = view.findViewById(R.id.search_game_year);
            textView.setText("제작년도: " + getArguments().getString("year") + "년");

            textView = view.findViewById(R.id.search_game_playtime);
            if (getArguments().getString("minplaytime") == getArguments().getString("maxplaytime"))
                textView.setText("플레이 시간: " + getArguments().getString("minplaytime") + "분");
            else
                textView.setText("플레이 시간: " + getArguments().getString("minplaytime") + "~" + getArguments().getString("maxplaytime") + "분");
            textView = view.findViewById(R.id.search_game_player);
            if (getArguments().getString("minplayer") == getArguments().getString("maxplayer"))
                textView.setText("플레이어 수: " + getArguments().getString("minplayer") + "명");
            else
                textView.setText("플레이어 수: " + getArguments().getString("minplayer") + "~" + getArguments().getString("maxplayer") + "명");
            textView = view.findViewById(R.id.search_game_age);
            textView.setText("연령: " + getArguments().getString("age") + "세 이상");
            textView = view.findViewById(R.id.search_game_publisher);
            textView.setText("제작사: " + getArguments().getString("publisher"));
            textView = view.findViewById(R.id.search_game_description);
            textView.setText("게임 설명: " + getArguments().getString("description").replace("<br/>", "\n"));
            textView.setMovementMethod(new ScrollingMovementMethod());
            ImageButton imageButton = view.findViewById(R.id.search_game_start);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), GameActivity.class);
                    intent.putExtra("name", getArguments().getString("name"));
                    intent.putExtra("id", getArguments().getString("id"));
                    startActivityForResult(intent, 0);
                }
            });

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL imageurl = new URL(getArguments().getString("imageurl"));
                        InputStream inputStream = imageurl.openStream();
                        bitmap[0] = BitmapFactory.decodeStream(inputStream);
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
            ImageView imageView = view.findViewById(R.id.search_game_image);
            imageView.setImageBitmap(bitmap[0]);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                ((MainActivity) getActivity()).FragmentGameUpdate();
                ((MainActivity) getActivity()).SetFragment("game");
            }
        }
    }
}
