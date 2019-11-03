package com.example.nomik.boardgamemanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentSearch extends Fragment {
    EditText editText;
    ListView search_ListView;
    ListView recent_ListView;
    ListView realTime_ListView;
    String url_String;
    String myName;
    ArrayList<FragmentSearchData> data = new ArrayList<>();
    ArrayList<FragmentSearchData> recentData = new ArrayList<>();
    ArrayList<FragmentSearchData> realTimeData = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        editText = view.findViewById(R.id.editText_search);
        search_ListView = view.findViewById(R.id.search_ListView);
        recent_ListView = view.findViewById(R.id.search_recent_ListView);
        realTime_ListView = view.findViewById(R.id.search_RealTime_ListView);
        myName = ((MainActivity)getActivity()).getMyName();
        TextView textView = view.findViewById(R.id.textView_Search_Myname);
        textView.setText("안녕하세요. " + myName + "님!");
        textView = view.findViewById(R.id.textView_Search_Random);
        textView.setText(getRandomString());

        search_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(search_ListView.getWindowToken(), 0);
                ((MainActivity)getActivity()).addRecentSearch(data.get(position).getName(), data.get(position).getGameid());
                ((MainActivity)getActivity()).SetFragment("search_game", data.get(position).getName(), data.get(position).getGameid());
                editText.setText("");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 500;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(final Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        url_String = "https://www.boardgamegeek.com/xmlapi/search?search=" + s;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                searchString(url_String);
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sortData(s.toString());
                        final FragmentSearchAdapter fragmentSearchAdapter = new FragmentSearchAdapter(data);
                        fragmentSearchAdapter.notifyDataSetChanged();
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    search_ListView.setAdapter(fragmentSearchAdapter);
                                }
                            });
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }, DELAY);
            }
        });

        recentData = ((MainActivity)getActivity()).getRecentSearch();
        FragmentSearchRecentAdapter fragmentSearchRecentAdapter = new FragmentSearchRecentAdapter(recentData);
        fragmentSearchRecentAdapter.notifyDataSetChanged();
        recent_ListView.setAdapter(fragmentSearchRecentAdapter);
        recent_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity()).SetFragment("search_game", recentData.get(i).getName(), recentData.get(i).getGameid());
            }
        });
        Thread thread = new Thread(){
            @Override
            public void run() {
                setRealTimeData();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FragmentSearchRecentAdapter fragmentSearchRealTimeAdapter = new FragmentSearchRecentAdapter(realTimeData);
        fragmentSearchRealTimeAdapter.notifyDataSetChanged();
        realTime_ListView.setAdapter(fragmentSearchRealTimeAdapter);
        realTime_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity()).SetFragment("search_game", realTimeData.get(i).getName(), realTimeData.get(i).getGameid());
            }
        });

        return view;
    }

    public void searchString(String s)  {
        data.clear();
        InputStream inputStream = null;
        try {
            URL url= new URL(s);
            String name = null;
            String gameid = null;
            String year = null;

            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            inputStream = url.openStream();
            parser.setInput(new InputStreamReader(inputStream));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String startTag = parser.getName();
                        if(startTag.equals("boardgame")) {
                            gameid = parser.getAttributeValue(0);
                        }
                        if(startTag.equals("name")) {
                            name = parser.nextText();
                        }
                        if(startTag.equals("yearpublished")) {
                            year = parser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        String endTag = parser.getName();
                        if(endTag.equals("boardgame")) {
                            FragmentSearchData fragmentSearchData = new FragmentSearchData(name, gameid, year);
                            data.add(fragmentSearchData);
                        }
                        break;
                    }
                }
                eventType = parser.next();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sortData(String s) {
        final String name = s.toLowerCase();
        Collections.sort(data, new Comparator<FragmentSearchData>() {
            @Override
            public int compare(FragmentSearchData d1, FragmentSearchData d2) {
                String n1 = d1.getName().toLowerCase();
                String n2 = d2.getName().toLowerCase();
                if(n1.startsWith(name) || n2.startsWith(name)) {
                    if(n1.startsWith(name) && n2.startsWith(name)){
                        return d1.getName().compareTo(d2.getName());
                    } else if(n1.startsWith(name)) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return d1.getName().compareTo(d2.getName());
                }
            }
        });

    }

    public void setRealTimeData() {
        realTimeData.clear();
        ServerConnect serverConnect = new ServerConnect("getRealTimeSearch");
        final JSONArray result = serverConnect.getJSONArray();
        try {
            if(result.getString(0).equals("success")) {
                JSONArray jsonArray = result.getJSONArray(1);
                if(jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject game = jsonArray.getJSONObject(i);
                        FragmentSearchData fragmentSearchData = new FragmentSearchData(game.getString("name"), game.getString("id"));
                        realTimeData.add(fragmentSearchData);
                    }
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
    }

    private String getRandomString(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("오늘은 어떤 게임이 하고싶으세요?");
        strings.add("오늘 기분은 어때요?");
        strings.add("오늘은 스플랜더를 해보는게 어때요?");
        strings.add("오늘은 정령섬을 해보는게 어때요?");
        strings.add("저는 보드게임만 있으면 행복해요.");
        strings.add("내일은 보드게임 모임이 있었으면 좋겠어요.");
        strings.add("보드게임...좋아하세요?");
        strings.add("보드게임이...하고싶어요.");
        strings.add("목숨 같은게 소중한게 아니야!\n보드게임이 소중한거라고!");
        strings.add("보드게임은...죽은거지?!");
        strings.add("넌 내게 보드게임을 줬어.");
        strings.add("보드게임방에서 기다릴게.");
        strings.add("인간이 5명이나 모이면\n반드시 1명은 보드게임을 한다.");
        strings.add("그냥...다 졌으면 좋겠다.");

        Random random = new Random();
        return strings.get(random.nextInt(strings.size()));
    }
}
