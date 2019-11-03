package com.example.nomik.boardgamemanager;

public class TrackerItem{
    private String name;
    private int color;
    private int value;
    private int change;
    private boolean selected;

    public TrackerItem() {
        color = 0;
        value = 50;
        change = 5;
        selected = false;
    }

    public void colorUp() {
        if(color < 7) {
            color++;
        } else{
            color = 0;
        }
    }

    public void colorDown() {
        if(color > 0) {
            color--;
        } else{
            color = 7;
        }
    }

    public void valueUp() {
        value += change;
    }

    public void valueDown() {
        value -= change;
    }

    public void changeUp() {
        change += 1;
    }

    public void changeDown() {
        change -= 1;
    }

    public int getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public int getChange() {
        return change;
    }
    public boolean getSelecet(){
        return selected;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean select(){
        selected = !selected;
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
