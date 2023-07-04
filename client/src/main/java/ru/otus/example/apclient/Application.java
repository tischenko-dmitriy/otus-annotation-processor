package ru.otus.example.apclient;

import ru.otus.example.apclient.data.Person;

public class Application {

    public static void main(String... args) {
        Person person = new Person("John", "Smith", 40);
        System.out.printf("Hello, %s!\n", person.getFirstName());
    }

}
