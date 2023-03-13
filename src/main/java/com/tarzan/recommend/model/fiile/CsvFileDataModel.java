package com.tarzan.recommend.model.fiile;

import com.google.common.base.Charsets;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.io.*;

/**
 * @author Lenovo
 */
public class CsvFileDataModel extends FileDataModel {

    public CsvFileDataModel(File dataFile) throws IOException {
        super(dataFile);
    }


    @Override
    public void setPreference(long userID, long itemID, float value) {
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(super.getDataFile(),true), Charsets.UTF_8)) {
            String  newline = userID+","+itemID+","+value;
            writer.write(newline);
            writer.write('\n');
        } catch (IOException ioe){
            System.out.println(ioe);
        }
    }






}
