package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Person;

import java.util.List;

public class GetPerson extends Message{

    public Person getPerson() {
        return person;
    }

    private final Person person;

    public GetPerson(String ok, String ras, Person person) {
        super(ok,ras);
        this.person = person;
    }
}
