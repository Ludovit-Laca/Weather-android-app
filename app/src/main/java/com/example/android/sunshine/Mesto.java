package com.example.android.sunshine;

public class Mesto {

    private int ID;
    private String nazov;

    public Mesto(int ID, String nazov) {
        this.ID = ID;
        this.nazov = nazov;
    }

    public int getID() {
        return ID;
    }

    public String getNazov() {
        return nazov;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }
}
