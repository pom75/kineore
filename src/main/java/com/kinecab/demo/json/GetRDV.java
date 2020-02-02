package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Event;

import java.util.List;

public class GetRDV extends Message {
    public List<Event> getRdvs() {
        return rdvs;
    }

    private final List<Event> rdvs;

    public GetRDV(String ok, String ras, List<Event> rdvs) {
        super(ok,ras);
        this.rdvs = rdvs;
    }
}
