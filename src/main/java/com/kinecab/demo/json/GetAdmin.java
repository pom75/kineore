package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAdmin extends Message{
    public List<Admin> getAdmins() {
        return admins;
    }

    private final List<Admin> admins;
    private final Map<String, String> mapIdAdminIdCab;

    public GetAdmin(String ok, String ras, List<Admin> admins) {
        super(ok,ras);
        this.admins = admins;
        mapIdAdminIdCab =  new HashMap<>();
    }

    public void addMapIdAdminIdCab(String idAdmin, String idCab){
        mapIdAdminIdCab.put(idAdmin,idCab);
    }

    public Map<String, String> getMapIdAdminIdCab() {
        return mapIdAdminIdCab;
    }
}
