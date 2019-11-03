package com.example.nomik.boardgamemanager;

import androidx.annotation.Nullable;

public class FragmentSearchData{
    private String name;
    private String gameid;
    private String year;

    FragmentSearchData(String _name, String _gameid) {
        name = _name;
        gameid = _gameid;
    }

    FragmentSearchData(String _name, String _gameid, String _year) {
        name = _name;
        gameid = _gameid;
        year = _year;
    }

    String getName() {
        return name;
    }

    String getGameid() {
        return gameid;
    }

    String getYear() {
        return year;
    }

    void setName(String _name) {
        name = _name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj.getClass() == FragmentSearchData.class)
            return name.equals(((FragmentSearchData)obj).getName());
        return super.equals(obj);
    }
}
