package ru.example.otus.ap.client;

import ru.example.otus.ap.annotations.ShowValues;

public class Person {

    private String name;
    private String surname;
    private Integer age;

public String showValues() {
	String result = "Person:
		age = this.age;
		name = this.name;
		surname = this.surname";
	return result;
}
}

