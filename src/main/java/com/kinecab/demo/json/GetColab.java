package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Colab;

import java.util.List;

public class GetColab extends Message{
    public List<Colab> getColabs() {
        return colabs;
    }

    private final List<Colab> colabs;

    public GetColab(String ok, String ras, List<Colab> colabs) {
        super(ok,ras);
        this.colabs = colabs;
    }


}
