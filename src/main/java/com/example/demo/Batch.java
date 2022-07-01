package com.example.demo;

import org.springframework.web.bind.annotation.PathVariable;

public class Batch {
    public Batch(String Name, String Path) {
        this.Path = Path;
        this.Name = Name;
    }
    public String Path;
    public String Name;
}
