package com.tarzan.recommend.model.fiile;

import org.apache.commons.codec.Charsets;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterable;

import java.io.*;

/**
 * @author Lenovo
 */
public class BXDataModel extends FileDataModel {

    public BXDataModel(File ratingsFile) throws IOException {
        super(convertFile(ratingsFile));
    }


    private static File convertFile(File sourceFile) throws IOException{
        File resultFile = new File(System.getProperty("java.io.tmpdir"), "prefs.csv");
        if (resultFile.exists()){
            resultFile.delete();
        }
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(resultFile), Charsets.UTF_8)) {
            for (String line: new FileLineIterable(sourceFile, false)){
                String convertedLine = line.replaceAll("[\t]", ",");
                writer.write(convertedLine);
                writer.write('\n');
            }
        } catch (IOException ioe){
            resultFile.delete();
            throw ioe;
        }
        return resultFile;
    }

}
