package com.example.android_sante;

import java.util.HashMap;
import java.util.List;

public class Food {
    private String typeFood;
    private String nameFood;
    private HashMap<String, Integer> caloriesperunits; // Nutritional information: calories per unit of food
    private String chosenUnit; // The chosen unit for this food
    private float quantity; // The quantity for the chosen unit

    public Food(String typeFood, String nameFood, HashMap<String, Integer> nutriments, String chosenUnit, float quantity) {
        this.typeFood = typeFood;
        this.nameFood = nameFood;
        this.caloriesperunits = nutriments;
        this.chosenUnit = chosenUnit;
        this.quantity = quantity;
    }

    public Food(String typeFood, String nameFood, HashMap<String, Integer> caloriesperunits) {
        this.typeFood = typeFood;
        this.nameFood = nameFood;
        this.caloriesperunits = caloriesperunits;
    }

    // Getters and setters for the new fields
    public String getChosenUnit() {
        return chosenUnit;
    }

    public void setChosenUnit(String chosenUnit) {
        this.chosenUnit = chosenUnit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    // Existing getters and setters
    public String getTypeFood() {
        return typeFood;
    }

    public void setTypeFood(String typeFood) {
        this.typeFood = typeFood;
    }

    public String getNameFood() {
        return nameFood;
    }

    public void setNameFood(String nameFood) {
        this.nameFood = nameFood;
    }

    public HashMap<String, Integer> getCaloriesperunits() {
        return caloriesperunits;
    }

    public void setCaloriesperunits(HashMap<String, Integer> caloriesperunits) {
        this.caloriesperunits = caloriesperunits;
    }

    public List<Food> getmeat() {
        return null;
    }
    public List<Food> getfish() {
        return null;
    }
    public List<Food> getvegetable() {
        return null;
    }
    public List<Food> getcereal() {
        return null;
    }
}
