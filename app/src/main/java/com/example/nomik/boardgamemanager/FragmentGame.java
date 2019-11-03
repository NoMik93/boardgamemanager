package com.example.nomik.boardgamemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import static android.content.Context.TELEPHONY_SERVICE;

public class FragmentGame extends Fragment {
    ArrayList<FragmentGameItem> data = new ArrayList<>();
    ArrayList<Game> gameList = new ArrayList<>();
    ArrayList<Player> playerList = new ArrayList<>();
    ListView listView;
    PieChart mWinChart;
    PieChart gameChart;
    PieChart aWinChart;
    int mWinGameNum = 0;
    String myName;

    private class Game implements Comparable<Game> {
        private String name;
        private int num;
        public Game(String name) {
            this.name = name;
            this.num = 1;
        }

        public String getName() {
            return name;
        }
        public int getNum() {
            return num;
        }
        public void addNum() {
            num++;
        }
        public void addNum(int num) { this.num += num; }
        public void setNum(int num) { this.num = num; }
        @Override
        public boolean equals(@androidx.annotation.Nullable Object obj) {
            return this.name.equals(((Game)obj).getName());
        }

        @Override
        public int compareTo(Game game) {
            return game.getNum() - this.num;
        }
    }
    private class Player implements Comparable<Player> {
        private String name;
        private int num;

        public Player(String name, int num) {
            this.name = name;
            this.num = num;
        }
        public int getNum() {
            return num;
        }
        public String getName() {
            return name;
        }
        public void addNum() {
            num++;
        }
        public void addNum(int num) {
            this.num += num;
        }
        @Override
        public boolean equals(@androidx.annotation.Nullable Object obj) {
            return this.name.equals(((Player)obj).getName());
        }

        @Override
        public int compareTo(Player player) {
            return player.getNum() - this.num;
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_game, container, false);
        myName = ((MainActivity)getActivity()).getMyName();
        listView = view.findViewById(R.id.listView_game);
        mWinChart = view.findViewById(R.id.chart_MyWinRate);
        gameChart = view.findViewById(R.id.chart_GameRate);
        aWinChart = view.findViewById(R.id.chart_AllWinRate);
        data = new ArrayList<>();
        mWinGameNum = 0;
        Thread thread = new Thread() {
            @Override
            public void run() {getGameData();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SetChart();
        return view;
    }
    private void getGameData() {
        gameList.clear();
        playerList.clear();
        ServerConnect serverConnect = new ServerConnect("getGameRecord", getPhoneNumber());
        JSONArray result = serverConnect.getJSONArrayWithSend();
        try {
            if(result.getString(0).equals("success")) {
                JSONArray jsonArray = result.getJSONArray(1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject gameRecord = jsonArray.getJSONObject(i);
                    FragmentGameItem fragmentGameItem = new FragmentGameItem(gameRecord.getString("gameId"), gameRecord.getString("gameName"), gameRecord.getString("date"), gameRecord.getString("dateTime"), gameRecord.getString("playTime"));
                    Game game = new Game(gameRecord.getString("gameName"));
                    if(gameList.contains(game)) {
                        int index = gameList.indexOf(game);
                        gameList.get(index).addNum();
                    } else {
                        gameList.add(game);
                    }
                    JSONArray players = gameRecord.getJSONArray("playerData");
                    ArrayList<Player> list = new ArrayList<>();
                    for (int j = 0; j < players.length(); j++) {
                        JSONObject player = players.getJSONObject(j);
                        fragmentGameItem.addPlayer(player.getString("playerName"), player.getInt("score"));
                        list.add(new Player(player.getString("playerName"), player.getInt("score")));
                    }
                    Collections.sort(list);
                    if(list.get(0).getName().equals(myName)) {
                        mWinGameNum++;
                    }
                    if(playerList.contains(list.get(0))) {
                        int index = playerList.indexOf(list.get(0));
                        playerList.get(index).addNum();
                    } else {
                        playerList.add(list.get(0));
                    }
                    data.add(fragmentGameItem);
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final FragmentGameAdapter fragmentGameAdapter = new FragmentGameAdapter(data);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(fragmentGameAdapter);
            }
        });
    }

    private void SetChart() {
        Collections.sort(gameList);
        Collections.sort(playerList);

        mWinChart.setUsePercentValues(false); // 그래프에 퍼센트 표시
        //mWinChart.getLegend().setEnabled(false);
        ArrayList<PieEntry> mwEntry = new ArrayList<>(); // 그래프에 표시할 값들 저장
        mwEntry.add(new PieEntry(mWinGameNum, "승"));
        mwEntry.add((new PieEntry((data.size() - mWinGameNum), "패")));
        Description mwDescription = new Description();
        mwDescription.setText("나의 승률"); // 그래프 라벨
        mwDescription.setTextSize(30f);
        mWinChart.setDescription(mwDescription); // 원 그래프에 설명을 붙임
        PieDataSet mwDataSet = new PieDataSet(mwEntry, "");
        mwDataSet.setSliceSpace(3f);
        mwDataSet.setSelectionShift(5f);
        mwDataSet.setColors(ColorTemplate.PASTEL_COLORS); // 그래프 색깔 템플릿 설정
        PieData mwData = new PieData(mwDataSet);
        mwData.setValueTextSize(30f);
        mwData.setValueTextColor(Color.BLACK); // 그래프 수치 색은 검은색
        mWinChart.setEntryLabelColor(Color.BLACK); // 그래프 수치의 이름 색은 검은색
        mWinChart.setData(mwData);
        mWinChart.getLegend().setEnabled(false); // 아래 항목 제거
        mWinChart.setEntryLabelTextSize(30f); // 그래프 항목 이름 크기

        gameChart.setUsePercentValues(false);
        ArrayList<PieEntry> gEntry = new ArrayList<>();
        if(gameList.size() > 4) {
            for(int i = 0; i < 4; i++) {
                PieEntry p = new PieEntry(gameList.get(i).getNum(), gameList.get(i).getName());
                gEntry.add(p);
            }
            Game other = new Game("그 외");
            other.setNum(0);
            for(int i = 4; i < gameList.size(); i++) {
                other.addNum(gameList.get(i).getNum());
            }
            PieEntry p = new PieEntry(other.getNum(), other.getName());
            gEntry.add(p);
        } else {
            for(int i = 0; i < gameList.size(); i++) {
                PieEntry p = new PieEntry(gameList.get(i).getNum(), gameList.get(i).getName());
                gEntry.add(p);
            }
        }
        Description gDescription = new Description();
        gDescription.setText("게임별 비율");
        gDescription.setTextSize(30f);
        gameChart.setDescription(gDescription);
        PieDataSet gDataSet = new PieDataSet(gEntry, "");
        gDataSet.setSliceSpace(3f);
        gDataSet.setSelectionShift(5f);
        gDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData gData = new PieData(gDataSet);
        gData.setValueTextSize(30f);
        gData.setValueTextColor(Color.BLACK);
        gameChart.setEntryLabelColor(Color.BLACK);
        gameChart.setData(gData);
        gameChart.getLegend().setEnabled(false);
        gameChart.setEntryLabelTextSize(30f);

        aWinChart.setUsePercentValues(false);
        ArrayList<PieEntry> awEntry = new ArrayList<>();
        if(playerList.size() > 4) {
            for(int i = 0; i < 4; i++) {
                PieEntry p = new PieEntry(playerList.get(i).getNum(), playerList.get(i).getName());
                awEntry.add(p);
            }
            Player other = new Player("그 외", 0);
            for(int i = 4; i < playerList.size(); i++) {
                other.addNum(playerList.get(i).getNum());
            }
            PieEntry p = new PieEntry(other.getNum(), other.getName());
            awEntry.add(p);
        } else {
            for(int i = 0; i < playerList.size(); i++) {
                PieEntry p = new PieEntry(playerList.get(i).getNum(), playerList.get(i).getName());
                awEntry.add(p);
            }
        }
        Description awDescription = new Description();
        awDescription.setText("각각의 승률");
        awDescription.setTextSize(30f);
        aWinChart.setDescription(awDescription);
        PieDataSet awDataSet = new PieDataSet(awEntry, "");
        awDataSet.setSliceSpace(3f);
        awDataSet.setSelectionShift(5f);
        awDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData awData = new PieData(awDataSet);
        awData.setValueTextSize(30f);
        awData.setValueTextColor(Color.BLACK);
        aWinChart.setEntryLabelColor(Color.BLACK);
        aWinChart.setData(awData);
        aWinChart.getLegend().setEnabled(false);
        aWinChart.setEntryLabelTextSize(30f);
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
