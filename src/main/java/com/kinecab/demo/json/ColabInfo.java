package com.kinecab.demo.json;

public class ColabInfo {
    private int idColab;
    private String colabName;
    private String colabDays;

    public ColabInfo(int idColab, String colabName, String colabDays) {
        this.idColab = idColab;
        this.colabName = colabName;
        this.colabDays = colabDays;
    }

    public String getColabDays() {
        return colabDays;
    }

    public void setColabDays(String colabDays) {
        this.colabDays = colabDays;
    }

    public int getIdColab() {
        return idColab;
    }

    public void setIdColab(int idColab) {
        this.idColab = idColab;
    }

    public String getColabName() {
        return colabName;
    }

    public void setColabName(String colabName) {
        this.colabName = colabName;
    }
}
