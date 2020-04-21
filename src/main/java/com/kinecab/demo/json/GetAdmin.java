package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Admin;

import java.util.List;

public class GetAdmin extends Message{
    public List<Admin> getAdmins() {
        return admins;
    }

    private final List<Admin> admins;

    public GetAdmin(String ok, String ras, List<Admin> admins) {
        super(ok,ras);
        this.admins = admins;
    }
}
