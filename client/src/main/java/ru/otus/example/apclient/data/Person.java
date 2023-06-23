package ru.otus.example.apclient.data;

import ru.otus.example.ap.annotations.CustomToString;

@CustomToString
public class Person {

    private String firstName;
    private String surname;
    private int age;

    public Person() {
    }

    public Person(String firstName, String surname, int age) {
        this.firstName = firstName;
        this.surname = surname;
        this.age = age;
    }

}
