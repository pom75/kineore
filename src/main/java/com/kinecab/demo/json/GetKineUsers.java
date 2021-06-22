package com.kinecab.demo.json;

import java.util.List;

public class GetKineUsers extends Message{

    private final List<ColabInfo> colabInfos;

    public GetKineUsers(String ok, String ras, List<ColabInfo> kineUsers) {
        super(ok,ras);
        this.colabInfos = kineUsers;
    }

    public List<ColabInfo> getColabInfos() {
        return colabInfos;
    }
}
