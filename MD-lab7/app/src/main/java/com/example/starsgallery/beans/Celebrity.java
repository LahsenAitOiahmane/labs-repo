package com.example.starsgallery.beans;

public class Celebrity {
    private int recordId;
    private String fullName;
    private String imagePath;
    private float score;
    private static int serialGenerator = 0;

    public Celebrity(String fullName, String imagePath, float score) {
        this.recordId = ++serialGenerator;
        this.fullName = fullName;
        this.imagePath = imagePath;
        this.score = score;
    }

    public int getRecordId() { return recordId; }
    public String getFullName() { return fullName; }
    public String getImagePath() { return imagePath; }
    public float getScore() { return score; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setScore(float score) { this.score = score; }
}
