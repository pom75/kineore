package com.kinecab.demo.json;

public class ColabInfo {
    private int idColab;
    private String colabName;

    public ColabInfo(int idColab, String colabName) {
        this.idColab = idColab;
        this.colabName = colabName;
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
