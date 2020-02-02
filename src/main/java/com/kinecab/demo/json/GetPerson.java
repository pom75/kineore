package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Person;

import java.util.List;

public class GetPerson extends Message{
    public List<Person> getPeople() {
        return people;
    }

    private final List<Person> people;

    public GetPerson(String ok, String ras, List<Person> people) {
        super(ok,ras);
        this.people = people;
    }
}
