package com.example.android_sante;

import java.util.List;

public class Lunch {
    private int id;
    private List<Food> lunchFoods;
    private int totaleCalories;
    private String date;

    public Lunch(String date, int totaleCalories, int idUnitChoose, List<Food> lunchFoods, int id) {
        this.date = date;
        this.totaleCalories = totaleCalories;
        this.lunchFoods = lunchFoods;
        this.id = id;
    }

    public Lunch() {
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Food> getLunchFoods() {
        return lunchFoods;
    }

    public void setLunchFoods(List<Food> lunchFoods) {
        this.lunchFoods = lunchFoods;
    }

    public int getTotaleCalories() {
        return totaleCalories;
    }

    public void setTotaleCalories(int totaleCalories) {
        this.totaleCalories = totaleCalories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
