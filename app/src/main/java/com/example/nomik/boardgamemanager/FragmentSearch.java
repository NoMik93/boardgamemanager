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
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


public class FragmentSearch extends Fragment {
    EditText editText;
    ListView search_ListView;
    String url_String;
    ArrayList<FragmentSearchData> data = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        editText = view.findViewById(R.id.editText_search);
        search_ListView = view.findViewById(R.id.search_ListView);

        search_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).SetFragment("search_game", data.get(position).getName(), data.get(position).getGameid());
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 500;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { // editText에 글자가 입력될 때
                /*if (Pattern.matches("^[가-힣][가-힣]+.*$", s)) { // 한글 2글자 이상으로 시작되면
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
                    FragmentSearchAdapter fragmentSearchAdapter = new FragmentSearchAdapter(data);
                    fragmentSearchAdapter.notifyDataSetChanged();
                    search_ListView.setAdapter(fragmentSearchAdapter);
                } else if (Pattern.matches("^[a-zA-Z0-9]..+$", s)) { // 영어나 숫자는 3글자 이상으로 시작되면
                    url_String = "https://www.boardgamegeek.com/xmlapi/search?search=" + s;
                    Thread thread =  new Thread(new Runnable() {
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
                    FragmentSearchAdapter fragmentSearchAdapter = new FragmentSearchAdapter(data);
                    fragmentSearchAdapter.notifyDataSetChanged();
                    search_ListView.setAdapter(fragmentSearchAdapter);
                } else {
                    data.clear();
                    FragmentSearchAdapter fragmentSearchAdapter = new FragmentSearchAdapter(data);
                    fragmentSearchAdapter.notifyDataSetChanged();
                    search_ListView.setAdapter(fragmentSearchAdapter);
                }*/
            }

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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                search_ListView.setAdapter(fragmentSearchAdapter);
                            }
                        });
                    }
                }, DELAY);
            }
        });
        return view;
    }

    public void searchString(String s)  {
        data.clear();
        try {
            URL url= new URL(s);
            String name = null;
            String gameid = null;
            String year = null;

            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            InputStream inputStream = url.openStream();
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
}
