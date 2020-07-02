package com.kinecab.demo.json;

import com.kinecab.demo.db.entity.Person;

import java.util.List;

public class GetPeople extends Message{
    public List<Person> getPeople() {
        return people;
    }

    private final List<Person> people;

    public GetPeople(String ok, String ras, List<Person> people) {
        super(ok,ras);
        this.people = people;
    }
}
