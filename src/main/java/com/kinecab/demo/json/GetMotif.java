package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.MotifCab;

import java.util.List;

public class GetMotif extends Message {
    public List<MotifCab> getMotif() {
        return motif;
    }

    private final List<MotifCab> motif;

    public GetMotif(String ok, String ras, List<MotifCab> motifCabs) {
        super(ok,ras);
        this.motif = motifCabs;
    }
}
