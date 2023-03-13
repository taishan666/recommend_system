package com.tarzan.recommend.mongodb;

import com.tarzan.recommend.dto.UserPreferenceDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Lenovo
 */
@Component
@Slf4j
@AllArgsConstructor
public class MongoAsyncService {

    private  final MongoTemplate mongoTemplate;

    @Async
    public void saveBatch(List<UserPreferenceDTO> list,String collectionName,int count){
        long old=System.currentTimeMillis();
        mongoTemplate.insert(list,collectionName);
        log.info("Processed {} lines ,耗时 {} ms", count,(System.currentTimeMillis()-old));
    }



}
