package com.example.nomik.boardgamemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentSearchRecentAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<FragmentSearchData> data;
    private int nList;

    public FragmentSearchRecentAdapter(ArrayList<FragmentSearchData> _data) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            final Context context = viewGroup.getContext();
            if (inflater == null)
                inflater = (LayoutInflater)context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            view = inflater.inflate(R.layout.fragment_search_listview_item, viewGroup, false);
        }

        TextView _itemText = view.findViewById(R.id.search_ListView_item_text);
        _itemText.setText(data.get(i).getName());
        return view;
    }
}
