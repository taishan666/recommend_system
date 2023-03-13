package com.tarzan.recommend.model.fiile;

import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.common.iterator.FileLineIterator;

import java.io.File;
import java.io.IOException;

/**
 * @author Lenovo
 */
@Slf4j
public class MyFileDataModel extends FileDataModel {

    public static DataModel model;
    public MyFileDataModel(File dataFile) throws IOException {
        super(dataFile);
    }

/*    static {
        try {
            model = new MyFileDataModel(new File("E:\\work_space\\recommend_system\\src\\main\\resources\\ml-latest\\ratings.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void processFile(FileLineIterator dataOrUpdateFileIterator, FastByIDMap<?> data, FastByIDMap<FastByIDMap<Long>> timestamps, boolean fromPriorData) {
        log.info("Reading file info...");
        int count = 0;
        while(dataOrUpdateFileIterator.hasNext()) {
            String line = dataOrUpdateFileIterator.next();
            if (!line.isEmpty()) {
                if(count!=0){
                    super.processLine(line, data, timestamps, fromPriorData);
                }
                ++count;
                if (count % 1000000 == 0) {
                    log.info("Processed {} lines", count);
                }
            }
        }

    }
}
