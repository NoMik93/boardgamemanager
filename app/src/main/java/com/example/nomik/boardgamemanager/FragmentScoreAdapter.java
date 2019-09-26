package com.example.nomik.boardgamemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentScoreAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<String> names;
    private ArrayList<Integer> scores;
    private int nList;

    public FragmentScoreAdapter(ArrayList<String> names, ArrayList<Integer> scores) {
        this.names = names;
        this.scores = scores;
        nList = names.size();
    }
    @Override
    public int getCount() {
        return nList;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final int index = i;
        if(view == null) {
            if(inflater == null)
                inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_score_listview_item, viewGroup, false);
        }
        TextView textView = view.findViewById(R.id.textView_score_item_name);
        textView.setText(names.get(i));
        final TextView textView_score = view.findViewById(R.id.textView_score_item_score);
        textView_score.setText(scores.get(i).toString());
        Button button = view.findViewById(R.id.button_score_item_minus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scores.set(index, scores.get(index).intValue() - 5);
                textView_score.setText(scores.get(index).toString());
                notifyDataSetChanged();
            }
        });
        button = view.findViewById(R.id.button_score_item_minus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scores.set(index, scores.get(index).intValue() - 1);
                textView_score.setText(scores.get(index).toString());
                notifyDataSetChanged();
            }
        });
        button = view.findViewById(R.id.button_score_item_plus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scores.set(index, scores.get(index).intValue() + 1);
                textView_score.setText(scores.get(index).toString());
                notifyDataSetChanged();
            }
        });
        button = view.findViewById(R.id.button_score_item_plus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scores.set(index, scores.get(index).intValue() + 5);
                textView_score.setText(scores.get(index).toString());
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
