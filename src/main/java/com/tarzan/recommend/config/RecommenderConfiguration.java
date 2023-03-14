package com.tarzan.recommend.config;

import lombok.extern.slf4j.Slf4j;
import org.lenskit.LenskitConfiguration;
import org.lenskit.config.ConfigHelpers;

import java.io.File;
import java.io.IOException;

/**
 * @author Lenovo
 */
@Slf4j
public class RecommenderConfiguration {


    public static LenskitConfiguration createConfig(String type){
        try {
            return ConfigHelpers.load(new File("etc/"+type+".groovy"));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
