package com.tarzan.recommend.recommender;

import com.tarzan.recommend.config.RecommenderConfigBuilder;
import com.tarzan.recommend.dao.FileDataDao;
import lombok.extern.slf4j.Slf4j;
import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.data.dao.DataAccessObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tarzan.recommend.enums.RecommendTypeEnum.*;


/**
 * @author TARZAN
 */
@Slf4j
@Component
public class RecommenderFactory {

    private static final Map<String, LenskitRecommender> RECOMMENDER_POOL = new ConcurrentHashMap<>(10);

    static {
       // RECOMMENDER_POOL.put(FUNK_SVD.getType(), getItemRecommender(FUNK_SVD.getType()));
      //  RECOMMENDER_POOL.put(ITEM_CF.getType(), getItemRecommender(ITEM_CF.getType()));
       // RECOMMENDER_POOL.put(USER_CF.getType(), getItemRecommender(USER_CF.getType()));
       // RECOMMENDER_POOL.put(SLOPE_ONE.getType(), getItemRecommender(SLOPE_ONE.getType()));
        RECOMMENDER_POOL.put(POPULAR.getType(), getRecommender(POPULAR.getType()));
    }

    public static LenskitRecommender getRecommender(LenskitConfiguration config, DataAccessObject dao){
        //最后，获取并使用该推荐器。
        try (LenskitRecommender rec = LenskitRecommender.build(config, dao)) {
            rec.close();
            log.info("obtained recommender from engine");
            //我们想要推荐项
            return rec;
        }
    }

    public static LenskitRecommender getRecommender(String type,DataAccessObject dao){
        LenskitConfiguration config= RecommenderConfigBuilder.createConfig(type);
        return getRecommender(config,dao);
    }

    public static LenskitRecommender getRecommender(String type){
        DataAccessObject dao=FileDataDao.get();
        if(RECOMMENDER_POOL.get(type)!=null){
            return RECOMMENDER_POOL.get(type);
        }else {
            LenskitRecommender recommender=getRecommender(type,dao);
            RECOMMENDER_POOL.put(type,recommender);
            return recommender;
        }
    }



    /**
     * 30分钟更新一下推荐引擎
     */
    @Scheduled(cron = "0 0/30 * * * ? ")
    private void reload() {
        DataAccessObject dao=FileDataDao.load();
        RECOMMENDER_POOL.forEach((k,v)-> RECOMMENDER_POOL.put(k,getRecommender(k,dao)));
    }

}
