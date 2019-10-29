package com.example.nomik.boardgamemanager;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

public class TrackerAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<TrackerItem> data;
    private int nList;

    public TrackerAdapter(ArrayList<TrackerItem> _data) {
        data = _data;
        nList = data.size();
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
    public View getView(int i, View view, final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final int index = i;
        if(view == null) {
            if(inflater == null)
                inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tracker_listview_item, viewGroup, false);
        }
        final View colorView = view.findViewById(R.id.View_tracker_color);
        final EditText editTextValue = view.findViewById(R.id.editText_tracker_value);
        final EditText editTextChange = view.findViewById(R.id.editText_tracker_change_value);
        if(i < data.size()) {
            colorView.setBackgroundColor(getColor(data.get(i).getColor()));
            editTextValue.setText(String.valueOf(data.get(i).getValue()));
            editTextChange.setText(String.valueOf(data.get(i).getChange()));
        }
        editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(""))
                    data.get(index).setValue(0);
                else
                    data.get(index).setValue(Integer.valueOf(editable.toString()));
            }
        });
        editTextChange.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(""))
                    data.get(index).setChange(0);
                else
                    data.get(index).setChange(Integer.valueOf(editable.toString()));
            }
        });
        final ImageButton imageButtonClose = view.findViewById(R.id.button_tracker_close);
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(index).select()) {
                    imageButtonClose.setImageResource(R.drawable.ic_check_box_black_24dp);
                } else {
                    imageButtonClose.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                }
            }
        });
        ImageButton imageButton = view.findViewById(R.id.button_tracker_color_down);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(index).colorDown();
                colorView.setBackgroundColor(getColor(data.get(index).getColor()));
            }
        });
        imageButton = view.findViewById(R.id.button_tracker_color_up);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(index).colorUp();
                colorView.setBackgroundColor(getColor(data.get(index).getColor()));
            }
        });
        imageButton = view.findViewById(R.id.button_tracker_value_down);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(index).valueDown();
                editTextValue.setText(String.valueOf(data.get(index).getValue()));
            }
        });
        imageButton = view.findViewById(R.id.button_tracker_value_up);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(index).valueUp();
                editTextValue.setText(String.valueOf(data.get(index).getValue()));
            }
        });
        imageButton = view.findViewById(R.id.button_tracker_change_value_down);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(index).changeDown();
                editTextChange.setText(String.valueOf(data.get(index).getChange()));
            }
        });
        imageButton = view.findViewById(R.id.button_tracker_change_value_up);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(index).changeUp();
                editTextChange.setText(String.valueOf(data.get(index).getChange()));
            }
        });
        return view;
    }

    int getColor(int num) {
        switch (num) {
            case 0:
                return Color.RED;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.rgb(153,0,153);
            case 5:
                return Color.GRAY;
            case 6:
                return Color.WHITE;
            case 7:
                return Color.BLACK;
            default:
                return Color.RED;
        }
    }
}
