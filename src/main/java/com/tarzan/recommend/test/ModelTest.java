package com.tarzan.recommend.test;


import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.io.IOException;

/**
 * @author Lenovo
 */
public class ModelTest {

    public static void main(String[] args) throws IOException {



        FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
        PreferenceArray User1Pref = new GenericUserPreferenceArray(2);
        User1Pref.setUserID(0, 1L);
        User1Pref.setItemID(0, 101L);
        User1Pref.setValue(0, 3.0f);
        User1Pref.setItemID(1, 102L);
        User1Pref.setValue(1, 4.0f);

        PreferenceArray User2Pref = new GenericUserPreferenceArray(2);
        User2Pref.setUserID(0, 2L);
        User2Pref.setItemID(0, 101L);
        User2Pref.setValue(0, 3.0f);
        User2Pref.setItemID(1, 102L);
        User2Pref.setValue(1, 4.0f);

        preferences.put(1L, User1Pref);
        preferences.put(2L, User2Pref);

        DataModel model = new GenericDataModel(preferences);
        model.refresh(null);
        System.out.println(model);



    }
}
