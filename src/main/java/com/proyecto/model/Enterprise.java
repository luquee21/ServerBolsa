package com.proyecto.model;

public class Enterprise {

    private int id;
    private String name;
    private int n_actions;

    public Enterprise(int id, String name, int n_actions) {
        this.id = id;
        this.name = name;
        this.n_actions = n_actions;
    }

    public Enterprise(String name, int n_actions) {
        this.name = name;
        this.n_actions = n_actions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getN_actions() {
        return n_actions;
    }

    public void setN_actions(int n_actions) {
        this.n_actions = n_actions;
    }

    @Override
    public String toString() {
        return "Enterprise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", n_actions=" + n_actions +
                "}\n";
    }
}
