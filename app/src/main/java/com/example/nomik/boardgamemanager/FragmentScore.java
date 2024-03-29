package com.example.nomik.boardgamemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import static android.content.Context.TELEPHONY_SERVICE;

public class FragmentScore extends Fragment {
    private ListView listView;
    private ArrayList<String> player;
    private ArrayList<Integer> score;
    private String gameID;
    private String gameName;
    private String time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_score, container, false);
        listView = view.findViewById(R.id.listView_score);
        gameID = ((GameActivity) getActivity()).getGameID();
        gameName= ((GameActivity) getActivity()).getGameName();
        time = ((GameActivity) getActivity()).getTime();
        player = ((GameActivity) getActivity()).getPlayers();
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
                ((GameActivity) getActivity()).SetFragment("complete");
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        saveData();
                    }
                };
                thread.start();
            }
        });
        return view;
    }

    private void saveData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("gameID", gameID);
            obj.put("gameName", gameName);
            obj.put("user", getPhoneNumber());
            obj.put("playTime", time);
            JSONArray players = new JSONArray();
            for (String p : player) {
                players.put(p);
            }
            JSONArray scores = new JSONArray();
            for (int s : score) {
                scores.put(s);
            }
            obj.put("players", players);
            obj.put("scores", scores);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerConnect serverConnect = new ServerConnect("saveGame", obj.toString());
        if(serverConnect.send()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) getContext().getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
        String pNumber = tm.getLine1Number();
        return pNumber;
    }
}
