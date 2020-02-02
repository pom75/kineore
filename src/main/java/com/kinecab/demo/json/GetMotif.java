package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Motif;

import java.util.List;

public class GetMotif extends Message {
    public List<Motif> getMotif() {
        return motif;
    }

    private final List<Motif> motif;

    public GetMotif(String ok, String ras, List<Motif> motifByIdAdmin) {
        super(ok,ras);
        this.motif = motifByIdAdmin;
    }
}
