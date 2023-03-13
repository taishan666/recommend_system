package com.tarzan.recommend.test;

import com.tarzan.recommend.model.fiile.CsvFileDataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import java.io.File;
import java.io.IOException;

/**
 * @author Lenovo
 */
public class CsvTest {

    public static void main(String[] args) throws IOException, TasteException {
        DataModel model=new CsvFileDataModel(new File("C:\\Users\\Lenovo\\Desktop\\userPrefs.csv"));
        long old=System.currentTimeMillis();
        for (int i = 0; i <100; i++) {
            model.setPreference(i,2L,2.1f);
        }
        System.out.println((System.currentTimeMillis()-old)+" ms");
    }
}
