package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.KineUser;

import java.util.List;

public class GetKineUsers extends Message{

    private final List<KineUser> kineUsers;

    public GetKineUsers(String ok, String ras, List<KineUser> kineUsers) {
        super(ok,ras);
        this.kineUsers = kineUsers;
    }

    public List<KineUser> getKineUsers() {
        return kineUsers;
    }
}
