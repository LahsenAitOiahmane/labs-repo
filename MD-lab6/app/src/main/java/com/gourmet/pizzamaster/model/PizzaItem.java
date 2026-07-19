package com.gourmet.pizzamaster.model;

public class PizzaItem {
    private static long idCounter = 100;

    private final long uniqueId;
    private String label;
    private double cost;
    private int drawableId;
    private String prepTime;
    private String components;
    private String bio;
    private String workflow;

    public PizzaItem() {
        this.uniqueId = idCounter++;
    }

    public PizzaItem(String label, double cost, int drawableId, String prepTime,
                   String components, String bio, String workflow) {
        this.uniqueId = idCounter++;
        this.label = label;
        this.cost = cost;
        this.drawableId = drawableId;
        this.prepTime = prepTime;
        this.components = components;
        this.bio = bio;
        this.workflow = workflow;
    }

    public long getUniqueId() { return uniqueId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public int getDrawableId() { return drawableId; }
    public void setDrawableId(int drawableId) { this.drawableId = drawableId; }
    public String getPrepTime() { return prepTime; }
    public void setPrepTime(String prepTime) { this.prepTime = prepTime; }
    public String getComponents() { return components; }
    public void setComponents(String components) { this.components = components; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getWorkflow() { return workflow; }
    public void setWorkflow(String workflow) { this.workflow = workflow; }

    @Override
    public String toString() {
        return label + " [" + cost + "$]";
    }
}
