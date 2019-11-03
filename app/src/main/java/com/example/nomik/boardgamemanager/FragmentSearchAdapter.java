package com.example.nomik.boardgamemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class FragmentSearchAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<FragmentSearchData> data;
    private int nList;

    public FragmentSearchAdapter(ArrayList<FragmentSearchData> _data) {
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
            convertView = inflater.inflate(R.layout.fragment_search_listview_item, parent, false);
        }

        TextView _itemText = convertView.findViewById(R.id.search_ListView_item_text);
        _itemText.setText(data.get(position).getName() + " (" + data.get(position).getYear() + ")");
        return convertView;
    }
}
