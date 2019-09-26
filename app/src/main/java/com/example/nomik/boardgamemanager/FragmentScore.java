package com.example.nomik.boardgamemanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentScore extends Fragment {
    ListView listView;
    ArrayList<String> player;
    ArrayList<Integer> score;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_score, container, false);
        listView = view.findViewById(R.id.listView_score);
        player = ((GameActivity)getActivity()).getPlayers();
        score = new ArrayList<>();
        for (int i = 0; i < player.size(); i++) {
            score.add(0);
        }
        FragmentScoreAdapter fragmentScoreAdapter = new FragmentScoreAdapter(player, score);
        listView.setAdapter(fragmentScoreAdapter);

        Button button = view.findViewById(R.id.Button_score_complete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).SetFragment("complete");
            }
        });
        return view;
    }
}
