package com.example.nomik.boardgamemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentGameAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<FragmentGameItem> data;
    private int nList;

    public FragmentGameAdapter(ArrayList<FragmentGameItem> _data) {
        data = _data;
        nList = data.size();
    }

    @Override
    public int getCount() {
        return nList;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null)
                inflater = (LayoutInflater)context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            convertView = inflater.inflate(R.layout.fragment_game_listview_item, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.textView_game_item_date);
        itemText.setText(data.get(position).getDate());
        itemText = convertView.findViewById(R.id.textView_game_item_date_time);
        itemText.setText(data.get(position).getDateTime());
        itemText = convertView.findViewById(R.id.textView_game_item_game_name);
        itemText.setText(data.get(position).getGameName());
        itemText = convertView.findViewById(R.id.textView_game_item_playerNum);
        itemText.setText(String.valueOf(data.get(position).getPlayerNum()) + "명");
        itemText = convertView.findViewById(R.id.textView_game_item_playtime);
        itemText.setText(data.get(position).getPlayTime());
        return convertView;
    }
}
