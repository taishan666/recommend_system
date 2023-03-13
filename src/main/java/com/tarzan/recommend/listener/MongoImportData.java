package com.tarzan.recommend.listener;

import com.google.common.base.Splitter;
import com.tarzan.recommend.dto.UserPreferenceDTO;
import com.tarzan.recommend.mongodb.MongoAsyncService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * @author tarzan
 */
@Slf4j
@Component
@Order(4)
@AllArgsConstructor
public class MongoImportData implements CommandLineRunner {

    private final MongoAsyncService mongoAsyncService;


    @Override
    public void run(String... args) {
  /*      InputStream in= Objects.requireNonNull(this.getClass().getResourceAsStream("/ml-latest/ratings.csv"));
        Splitter delimiterPattern= Splitter.on(",");
        long old=System.currentTimeMillis();
        int count = 0;
        List<UserPreferenceDTO> list= new ArrayList<>(10000);
        FileLineIterator iterator;
        try {
            iterator = new FileLineIterator(in,true);
            while (iterator.hasNext()){
                String line = iterator.next();
                Iterator<String> tokens =delimiterPattern.split(line).iterator();
                long userId=Long.parseLong(tokens.next());
                long itemId=Long.parseLong(tokens.next());
                float preference=Float.parseFloat(tokens.next());
                UserPreferenceDTO dto=new UserPreferenceDTO(userId,itemId,preference);
                list.add(dto);
                ++count;
                if (count % 1000000 == 0) {
                    mongoAsyncService.saveBatch(new ArrayList<>(list),"items",count);
                    list.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("结束耗时 "+(System.currentTimeMillis()-old)+" ms");*/
    }

}

