package com.example.apistreamlyambda;

import java.util.ArrayList;
import java.util.List;

public class Foo {
    private String name;
    List<Bar> bars = new ArrayList<>();

    Foo(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
