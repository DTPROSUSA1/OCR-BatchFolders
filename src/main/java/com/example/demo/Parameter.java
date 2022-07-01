package com.example.demo;

public class Parameter {
    public Parameter(String Name, String Value){
        this.Name = Name;
        this.Value = Value;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    private String Name;
    private String Value;
    public String getValue() {
        return Value;
    }
    public void setValue(String value) {
        Value = value;
    }
}
