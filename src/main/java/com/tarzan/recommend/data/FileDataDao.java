package com.tarzan.recommend.data;

import lombok.extern.slf4j.Slf4j;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Lenovo
 */
@Slf4j
public class FileDataDao {

    private static DataAccessObject dao;

    public static DataAccessObject get(){
        if(dao==null){
            Path dataFile = Paths.get("data/movielens.yml");
            StaticDataSource data = null;
            try {
                data = StaticDataSource.load(dataFile);
                dao= data.get();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return dao;
    }
}
