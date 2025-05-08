package com.example.android_sante;

import java.util.HashMap;

public class DataBody {
    private int id;
    private HashMap<String, Float> weights;
    private float weightGoal;

    public DataBody(int id, HashMap<String, Float> weights, float weightGoal) {
        this.id = id;
        this.weights = weights;
        this.weightGoal = weightGoal;
    }
    public DataBody() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<String, Float> getWeights() {
        return weights;
    }

    public void setWeights(HashMap<String, Float> weights) {
        this.weights = weights;
    }

    public float getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(float weightGoal) {
        this.weightGoal = weightGoal;
    }

    public void addWeight(String date, float weight) {
        this.weights.put(date, weight);
    }

    public int getWeightByDate(String date) {
        return this.weights.get(date).intValue();
    }

    public int getLastWeight() {
        String lastDate = "";
        for (String date : this.weights.keySet()) {
            if (lastDate.isEmpty() || date.compareTo(lastDate) > 0) {
                lastDate = date;
            }
        }
        return this.weights.get(lastDate).intValue();
    }
}
