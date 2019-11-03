package com.example.nomik.boardgamemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
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
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setTitle(data.get(position).getGameName());
                String message = "날짜: " + data.get(position).getDate() + ", " + data.get(position).getDateTime() + "\n";
                message += "걸린 시간: " + data.get(position).getPlayTime() + "\n";
                message += "플레이어:\n";
                for(int i = 0; i < data.get(position).getPlayerNum(); i++) {
                    message += "          " + data.get(position).getPlayerName(i) + " " + data.get(position).getPlayerScore(i) + "점\n";
                }
                builder.setMessage(message);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return convertView;
    }
}
