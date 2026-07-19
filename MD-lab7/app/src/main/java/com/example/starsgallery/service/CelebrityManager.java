package com.example.starsgallery.service;

import com.example.starsgallery.beans.Celebrity;
import com.example.starsgallery.dao.IDataAccess;
import java.util.ArrayList;
import java.util.List;

public class CelebrityManager implements IDataAccess<Celebrity> {
    private List<Celebrity> profileList;
    private static CelebrityManager vaultInstance;

    private CelebrityManager() {
        profileList = new ArrayList<>();
        populateVault();
    }

    public static CelebrityManager getVault() {
        if (vaultInstance == null) vaultInstance = new CelebrityManager();
        return vaultInstance;
    }

    private void populateVault() {
        profileList.add(new Celebrity("Brad Pitt", "file:///android_asset/celeIMG/celeb_face_hairstyle_1.jpg", 4.5f));
        profileList.add(new Celebrity("George Clooney", "file:///android_asset/celeIMG/celeb_face_hairstyle_2.jpg", 4.2f));
        profileList.add(new Celebrity("Johnny Depp", "file:///android_asset/celeIMG/celeb_face_hairstyle_3.jpg", 4.7f));
        profileList.add(new Celebrity("Tom Hardy", "file:///android_asset/celeIMG/celeb_face_hairstyle_4.jpg", 4.8f));
        profileList.add(new Celebrity("Robert Downey Jr", "file:///android_asset/celeIMG/celeb_face_hairstyle_5.jpg", 4.9f));
        profileList.add(new Celebrity("Chris Hemsworth", "file:///android_asset/celeIMG/celeb_face_hairstyle_6.jpg", 4.6f));
    }

    @Override public boolean add(Celebrity entity) { return profileList.add(entity); }
    @Override public boolean modify(Celebrity entity) {
        for (Celebrity c : profileList) {
            if (c.getRecordId() == entity.getRecordId()) {
                c.setFullName(entity.getFullName());
                c.setImagePath(entity.getImagePath());
                c.setScore(entity.getScore());
                return true;
            }
        }
        return false;
    }
    @Override public boolean remove(Celebrity entity) { return profileList.remove(entity); }
    @Override public Celebrity getById(int id) {
        for (Celebrity c : profileList) if (c.getRecordId() == id) return c;
        return null;
    }
    @Override public List<Celebrity> getAll() { return profileList; }
}
