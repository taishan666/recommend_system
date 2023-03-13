package com.tarzan.recommend.test;

import com.tarzan.recommend.model.fiile.CsvDataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import java.io.File;
import java.io.IOException;

public class CsvTest1 {

    public static void main(String[] args) throws IOException, TasteException {
      //  DataModel model=new CsvFileDataModel(new File("E:\\work_space\\recommend_system\\src\\main\\resources\\ml-latest\\ratings.csv"));
        DataModel model=new CsvDataModel(new File("C:\\Users\\Lenovo\\Desktop\\ratings.csv"),true);
        model.removePreference(1L,307L);
       // model.removePreference(6L,2L);
    }
}
