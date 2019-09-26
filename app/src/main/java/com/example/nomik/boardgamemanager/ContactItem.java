package com.example.nomik.boardgamemanager;

import java.io.Serializable;

public class ContactItem implements Serializable {
    public String name, number;
    public long photo_id = 0, person_id = 0;
    public boolean selected;

    public ContactItem() { selected = false; }

    @Override
    public String toString() {
        return this.number;
    }

    @Override
    public int hashCode() {
        return numberChanged().hashCode();
    }
    public String numberChanged() {
        return number.replace("-", "");
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ContactItem)
            return numberChanged().equals(((ContactItem) obj).numberChanged());
        return false;
    }
}
