package com.example.android.sunshine;

public class Pocasie {

    private int ID;
    private String datum;
    private String predpoved;
    private String stupen;
    private int ID_mesto;

    public Pocasie(int ID, String datum, String predpoved, String stupen, int ID_mesto) {
        this.ID = ID;
        this.datum = datum;
        this.predpoved = predpoved;
        this.stupen = stupen;
        this.ID_mesto = ID_mesto;
    }

    public int getID() {
        return ID;
    }

    public String getDatum() {
        return datum;
    }

    public String getPredpoved() {
        return predpoved;
    }

    public String getStupen() {
        return stupen;
    }

    public int getID_mesto() {
        return ID_mesto;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public void setPredpoved(String predpoved) {
        this.predpoved = predpoved;
    }

    public void setStupen(String stupen) {
        this.stupen = stupen;
    }

    public void setID_mesto(int ID_mesto) {
        this.ID_mesto = ID_mesto;
    }
}
