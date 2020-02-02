package com.kinecab.demo.json;

import java.util.Set;

public class BookRdv extends Message {
    public Set<Integer> getCollect() {
        return collect;
    }

    private final Set<Integer> collect;

    public BookRdv(String ok, String ras, Set<Integer> collect) {
        super(ok, ras);
        this.collect = collect;
    }
}
