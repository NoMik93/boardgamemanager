package com.example.nomik.boardgamemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


public class FragmentSearch extends Fragment {
    ClearEditText editText;
    ListView search_ListView;
    ListView recent_ListView;
    ListView realTime_ListView;
    String url_String;
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

        search_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    public void sortData(String s) {
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
        HttpURLConnection conn = null;
        try {
            String urlString = "http://192.168.0.174:8080/bgm/DBConnection";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("mode", "getRealTimeSearch");
            conn.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
            conn.setRequestProperty("Accept", "application/json");

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
                JSONArray jsonArray= new JSONArray(responseString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject game = jsonArray.getJSONObject(i);
                    FragmentSearchData fragmentSearchData = new FragmentSearchData(game.getString("name"), game.getString("id"));
                    realTimeData.add(fragmentSearchData);
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
}
